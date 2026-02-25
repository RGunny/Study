package me.rgunny.saga.coordinator

import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.coordinator.service.OrchestrationTransferService
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
 * Orchestration Saga 통합 테스트.
 *
 * MockRestServiceServer로 참여자(account-service, transaction-service, notification-service)
 * 응답을 모킹하여 코디네이터의 출금 → 입금 → 알림 흐름을 검증한다.
 */
@SpringBootTest
class OrchestrationIntegrationTest {

    @Autowired lateinit var sagaStateRepository: SagaStateRepository
    @Autowired lateinit var restTemplate: RestTemplate
    @Autowired lateinit var orchestrationTransferService: OrchestrationTransferService

    @Value("\${service.account.url}") lateinit var accountUrl: String
    @Value("\${service.transaction.url}") lateinit var transactionUrl: String
    @Value("\${service.notification.url}") lateinit var notificationUrl: String

    lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        sagaStateRepository.deleteAll()
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `Orchestration 성공 - 출금, 입금, 알림 모두 성공 시 COMPLETED`() {
        // Step 1: 출금 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"transactionId":"tx-001","status":"COMPLETED"}""", MediaType.APPLICATION_JSON))

        // Step 2: 입금 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/deposit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"depositId":"dp-001","status":"COMPLETED"}""", MediaType.APPLICATION_JSON))

        // Step 3: 알림 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$notificationUrl/internal/notification"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"status":"SENT"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = orchestrationTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("COMPLETED")
        assertThat(response.message).isEqualTo("Transfer successful")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.sagaType).isEqualTo("ORCHESTRATION")
        assertThat(sagaState.status).isEqualTo("COMPLETED")

        mockServer.verify()
    }

    @Test
    fun `Orchestration 성공 - 알림 실패해도 이체는 COMPLETED (best-effort)`() {
        // 출금 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"transactionId":"tx-001","status":"COMPLETED"}""", MediaType.APPLICATION_JSON))

        // 입금 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/deposit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"depositId":"dp-001","status":"COMPLETED"}""", MediaType.APPLICATION_JSON))

        // 알림 서버 에러
        mockServer.expect(ExpectedCount.once(), requestTo("$notificationUrl/internal/notification"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = orchestrationTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("COMPLETED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("COMPLETED")

        mockServer.verify()
    }

    @Test
    fun `Orchestration 실패 - 출금 실패(잔액 부족) 시 즉시 FAILED`() {
        // 출금 실패 (FAILED 응답)
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"transactionId":"","status":"FAILED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = orchestrationTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("Insufficient balance")

        // 출금 실패 시 SagaState가 생성되지 않음
        assertThat(sagaStateRepository.findById(response.sagaId)).isEmpty

        mockServer.verify()
    }

    @Test
    fun `Orchestration 실패 - 출금 서버 에러 시 즉시 FAILED`() {
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = orchestrationTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")

        assertThat(sagaStateRepository.findById(response.sagaId)).isEmpty

        mockServer.verify()
    }

    @Test
    fun `Orchestration 실패 - 입금 실패 시 보상 트랜잭션 실행 후 COMPENSATED`() {
        // 출금 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"transactionId":"tx-001","status":"COMPLETED"}""", MediaType.APPLICATION_JSON))

        // 입금 서버 에러
        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/deposit"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        // 보상: 출금 복원
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/withdraw/compensate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"transactionId":"","status":"COMPENSATED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = orchestrationTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("compensated")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("COMPENSATED")

        mockServer.verify()
    }
}
