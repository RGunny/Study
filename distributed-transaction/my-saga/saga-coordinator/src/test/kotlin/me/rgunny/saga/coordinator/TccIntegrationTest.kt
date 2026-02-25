package me.rgunny.saga.coordinator

import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.coordinator.service.TccTransferService
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
 * TCC (Try-Confirm-Cancel) 통합 테스트.
 *
 * 2PC/3PC와의 핵심 차이:
 * - Try 단계에서 frozen_amount로 "소프트 잠금" (비즈니스 레벨 예약)
 * - Confirm으로 확정, Cancel로 예약 해제
 * - 인프라(DB 트랜잭션)가 아닌 비즈니스 로직으로 일관성 보장
 */
@SpringBootTest
class TccIntegrationTest {

    @Autowired lateinit var sagaStateRepository: SagaStateRepository
    @Autowired lateinit var restTemplate: RestTemplate
    @Autowired lateinit var tccTransferService: TccTransferService

    @Value("\${service.account.url}") lateinit var accountUrl: String
    @Value("\${service.transaction.url}") lateinit var transactionUrl: String

    lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        sagaStateRepository.deleteAll()
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `TCC 성공 - Try 후 Confirm 완료`() {
        // Try Phase
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"RESERVED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"RESERVED"}""", MediaType.APPLICATION_JSON))

        // Confirm Phase
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"CONFIRMED"}""", MediaType.APPLICATION_JSON))

        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/tcc/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"CONFIRMED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = tccTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("CONFIRMED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.sagaType).isEqualTo("TCC")
        assertThat(sagaState.status).isEqualTo("CONFIRMED")

        mockServer.verify()
    }

    @Test
    fun `TCC 실패 - account try 실패 시 즉시 CANCELLED (잔액 부족)`() {
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"FAILED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = tccTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("Account try failed")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("CANCELLED")

        mockServer.verify()
    }

    @Test
    fun `TCC 실패 - transaction try 실패 시 account cancel 후 CANCELLED`() {
        // account try 성공
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"RESERVED"}""", MediaType.APPLICATION_JSON))

        // transaction try 실패
        mockServer.expect(ExpectedCount.once(), requestTo("$transactionUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"FAILED"}""", MediaType.APPLICATION_JSON))

        // account cancel 호출됨
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/cancel"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("""{"sagaId":"test","status":"CANCELLED"}""", MediaType.APPLICATION_JSON))

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = tccTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.message).contains("account cancelled")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("CANCELLED")

        mockServer.verify()
    }

    @Test
    fun `TCC 실패 - account try 서버 에러 시 CANCELLED`() {
        mockServer.expect(ExpectedCount.once(), requestTo("$accountUrl/internal/tcc/try"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError())

        val request = TransferRequest("1000-0001", "1000-0002", BigDecimal("10000"))
        val response = tccTransferService.executeTransfer(request)

        assertThat(response.status).isEqualTo("FAILED")

        val sagaState = sagaStateRepository.findById(response.sagaId).orElseThrow()
        assertThat(sagaState.status).isEqualTo("CANCELLED")

        mockServer.verify()
    }
}
