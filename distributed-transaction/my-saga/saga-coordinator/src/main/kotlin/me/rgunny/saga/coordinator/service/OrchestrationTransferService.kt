package me.rgunny.saga.coordinator.service

import me.rgunny.saga.coordinator.domain.SagaState
import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.common.dto.DepositRequest
import me.rgunny.saga.common.dto.DepositResponse
import me.rgunny.saga.common.dto.NotificationRequest
import me.rgunny.saga.common.dto.TransferRequest
import me.rgunny.saga.common.dto.TransferResponse
import me.rgunny.saga.common.dto.WithdrawRequest
import me.rgunny.saga.common.dto.WithdrawResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.UUID

/**
 * Orchestration Saga 패턴의 중앙 조정자(Coordinator).
 *
 * 각 단계를 동기 REST 호출로 직접 제어하며, SagaState로 진행 상태를 추적한다.
 *
 * 호출 흐름:
 * 1. 출금 요청 (Account Service REST 호출)
 * 2. 입금 요청 (Transaction Service REST 호출)
 * 3. 알림 전송 (Notification Service REST 호출, best-effort)
 *
 * 입금 실패 시 Account Service에 보상 요청을 보낸다.
 */
@Service
class OrchestrationTransferService(
    private val sagaStateRepository: SagaStateRepository,
    private val restTemplate: RestTemplate,
    @Value("\${service.account.url}") private val accountServiceUrl: String,
    @Value("\${service.transaction.url}") private val transactionServiceUrl: String,
    @Value("\${service.notification.url}") private val notificationServiceUrl: String
) {

    /**
     * Orchestration Saga로 계좌 이체를 실행한다.
     *
     * 출금 → 입금 → 알림 순서로 진행하며,
     * 입금 실패 시 보상 트랜잭션으로 출금을 복원한다.
     */
    @Transactional
    fun executeTransfer(request: TransferRequest): TransferResponse {
        val sagaId = UUID.randomUUID().toString()

        // Step 1: 출금 요청
        val withdrawResponse = requestWithdraw(sagaId, request)
            ?: return TransferResponse(sagaId, "FAILED", "Insufficient balance or account not found")

        val sagaState = createSagaState(sagaId, request)

        // Step 2: 입금 요청 (실패 시 보상)
        if (!requestDeposit(sagaId, request)) {
            compensateWithdraw(sagaId, request, sagaState)
            return TransferResponse(sagaId, "FAILED", "Deposit failed, withdrawal compensated")
        }

        // Step 3: 알림 (best-effort)
        sendNotification(sagaId, request)

        sagaState.status = "COMPLETED"
        sagaStateRepository.save(sagaState)
        return TransferResponse(sagaId, "COMPLETED", "Transfer successful")
    }

    /** Account Service에 출금을 요청한다. 실패 시 null 반환. */
    private fun requestWithdraw(sagaId: String, request: TransferRequest): WithdrawResponse? {
        return try {
            val withdrawRequest = WithdrawRequest(sagaId, request.fromAccountNumber, request.amount)
            val response = restTemplate.postForObject(
                "$accountServiceUrl/internal/withdraw",
                withdrawRequest,
                WithdrawResponse::class.java
            ) ?: return null
            if (response.status == "FAILED") null else response
        } catch (e: Exception) {
            null
        }
    }

    /** SagaState를 생성하고 저장한다. */
    private fun createSagaState(sagaId: String, request: TransferRequest): SagaState {
        val sagaState = SagaState(
            sagaId = sagaId,
            fromAccountNumber = request.fromAccountNumber,
            toAccountNumber = request.toAccountNumber,
            amount = request.amount,
            status = "STARTED"
        )
        sagaStateRepository.save(sagaState)
        return sagaState
    }

    /** Transaction Service에 입금을 요청한다. 성공 시 true, 실패 시 false. */
    private fun requestDeposit(sagaId: String, request: TransferRequest): Boolean {
        return try {
            val depositRequest = DepositRequest(
                sagaId = sagaId,
                accountNumber = request.toAccountNumber,
                amount = request.amount,
                fromAccountNumber = request.fromAccountNumber
            )
            restTemplate.postForObject(
                "$transactionServiceUrl/internal/deposit",
                depositRequest,
                DepositResponse::class.java
            ) ?: throw RuntimeException("Deposit response was null")
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Account Service에 출금 보상을 요청하고 Saga 상태를 갱신한다. */
    private fun compensateWithdraw(sagaId: String, request: TransferRequest, sagaState: SagaState) {
        try {
            val compensateRequest = WithdrawRequest(sagaId, request.fromAccountNumber, request.amount)
            restTemplate.postForObject(
                "$accountServiceUrl/internal/withdraw/compensate",
                compensateRequest,
                WithdrawResponse::class.java
            )
        } catch (e: Exception) {
            println("[COORDINATOR] Failed to compensate withdraw for saga $sagaId: ${e.message}")
        }
        sagaState.status = "COMPENSATED"
        sagaStateRepository.save(sagaState)
    }

    /** Notification Service에 알림을 전송한다. 실패해도 무시한다 (best-effort). */
    private fun sendNotification(sagaId: String, request: TransferRequest) {
        try {
            val notificationRequest = NotificationRequest(
                sagaId = sagaId,
                userId = request.fromAccountNumber,
                notificationType = "TRANSFER",
                message = "Transfer of ${request.amount} to ${request.toAccountNumber} completed"
            )
            restTemplate.postForObject(
                "$notificationServiceUrl/internal/notification",
                notificationRequest,
                String::class.java
            )
        } catch (_: Exception) {
            // best-effort: 알림 실패는 이체 결과에 영향 없음
        }
    }
}
