package me.rgunny.saga.coordinator

import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.coordinator.service.ThreePcTransferService
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
 * 3PC (Three-Phase Commit) 통합 테스트.
 *
 * 2PC와의 핵심 차이: CAN_COMMIT 단계가 추가되어 리소스 잠금 전에 참여 가능 여부를 확인한다.
 * PRE_COMMIT/DO_COMMIT은 2PC의 PREPARE/COMMIT 엔드포인트를 재사용한다.
 */
@SpringBootTest
class ThreePcIntegrationTest {

    @Autowired lateinit var sagaStateRepository: SagaStateRepository
    @Autowired lateinit var restTemplate: RestTemplate
    @Autowired lateinit var threePcTransferService: ThreePcTransferService

    @Value("\${service.account.url}") lateinit var accountUrl: String
    @Value("\${service.transaction.url}") lateinit var transactionUrl: String

    lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        sagaStateRepository.deleteAll()
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `3PC 성공 - CAN_COMMIT, PRE_COMMIT, DO_COMMIT 3단계 완료`() {
        // Phase 1: CAN_COMMIT - 양쪽 모두 vote=true
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        // Phase 2: PRE_COMMIT (= 2PC prepare 재사용)
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        // Phase 3: DO_COMMIT (= 2PC commit 재사용)
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"COMMITTED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"COMMITTED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = threePcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("COMMITTED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.sagaType).isEqualTo("THREE_PC")
        assertThat(sagaState.status).isEqualTo("COMMITTED")

        mockServer.verify()
    }

    @Test
    fun `3PC 실패 - CAN_COMMIT에서 account 거부 시 즉시 ABORTED (리소스 잠금 없음)`() {
        // account가 CAN_COMMIT 거부 (잔액 부족 예상)
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":false}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = threePcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("CAN_COMMIT rejected")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }

    @Test
    fun `3PC 실패 - PRE_COMMIT에서 transaction 실패 시 account rollback 후 ABORTED`() {
        // Phase 1: CAN_COMMIT 통과
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        // Phase 2: account prepare 성공, transaction prepare 실패
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"PREPARED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/2pc/prepare"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        // account rollback
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/2pc/rollback"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"ABORTED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = threePcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("account rolled back")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }

    @Test
    fun `3PC 실패 - CAN_COMMIT에서 서버 에러 시 ABORTED`() {
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/3pc/can-commit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"vote":true}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = threePcTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("ABORTED")

        mockServer.verify()
    }
}
