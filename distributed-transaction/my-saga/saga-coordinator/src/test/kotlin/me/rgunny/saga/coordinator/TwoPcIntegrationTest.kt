package me.rgunny.saga.coordinator

import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.common.dto.TransferRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

/**
 * 2PC (Two-Phase Commit) 통합 테스트.
 *
 * MockRestServiceServer로 참여자(account-service, transaction-service) 응답을 모킹하여
 * 코디네이터의 2단계 커밋 흐름을 검증한다.
 */
@SpringBootTest
class TwoPcIntegrationTest {

    @Autowired lateinit var sagaStateRepository: SagaStateRepository
    @Autowired lateinit var restTemplate: RestTemplate

    @Value("\${service.account.url}") lateinit var accountUrl: String
    @Value("\${service.transaction.url}") lateinit var transactionUrl: String

    lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        sagaStateRepository.deleteAll()
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Autowired lateinit var twoPcTransferService: me.rgunny.saga.coordinator.service.TwoPcTransferService

    @Test
    fun `2PC 성공 - 양쪽 모두 PREPARED 후 COMMITTED`() {
        // Phase 1: 양쪽 모두 PREPARED 응답
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        // Phase 2: 양쪽 모두 COMMITTED 응답
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"COMMITTED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"COMMITTED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = twoPcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("COMMITTED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.sagaType).isEqualTo("TWO_PC")
        assertThat(sagaState.status).isEqualTo("COMMITTED")

        mockServer.verify()
    }

    @Test
    fun `2PC 실패 - account prepare 실패 시 즉시 ABORTED`() {
        // account prepare 실패 (잔액 부족)
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"ABORT"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = twoPcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("Account prepare failed")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }

    @Test
    fun `2PC 실패 - transaction prepare 실패 시 account rollback 후 ABORTED`() {
        // account prepare 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        // transaction prepare 실패
        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"ABORT"}""", MediaType.APPLICATION_JSON))

        // account rollback 호출됨
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/rollback"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"ABORTED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = twoPcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("account rolled back")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }

    @Test
    fun `2PC 실패 - account prepare 서버 에러 시 ABORTED`() {
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = twoPcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }
}
