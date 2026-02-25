package me.rgunny.saga.transaction.service

import me.rgunny.saga.transaction.domain.Deposit
import me.rgunny.saga.transaction.domain.Transaction
import me.rgunny.saga.transaction.repository.DepositRepository
import me.rgunny.saga.transaction.repository.TransactionRepository
import me.rgunny.saga.common.dto.DepositRequest
import me.rgunny.saga.common.dto.DepositResponse
import me.rgunny.saga.common.dto.NotificationRequest
import me.rgunny.saga.common.dto.NotificationResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.util.*

/**
 * Orchestration Saga 참여자 서비스 (transaction-service 측).
 *
 * saga-coordinator가 REST 호출로 입금을 요청할 때 사용된다.
 */
@Service
class OrchestrationDepositService(
    private val transactionRepository: TransactionRepository,
    private val depositRepository: DepositRepository,
    private val restTemplate: RestTemplate,
    @Value("\${service.notification.url}") private val notificationServiceUrl: String
) {

    /** Orchestration 패턴에서 REST 호출로 입금을 처리한다. */
    @Transactional
    fun processDeposit(request: DepositRequest): DepositResponse {
        val transactionId = UUID.randomUUID().toString()
        val depositId = UUID.randomUUID().toString()

        val transaction = Transaction(
            transactionId = transactionId,
            sagaId = request.sagaId,
            fromAccountNumber = request.fromAccountNumber,
            toAccountNumber = request.accountNumber,
            amount = request.amount,
            status = "COMPLETED"
        )
        transactionRepository.save(transaction)

        val deposit = Deposit(
            depositId = depositId,
            transactionId = transactionId,
            accountNumber = request.accountNumber,
            amount = request.amount,
            status = "COMPLETED",
            sagaId = request.sagaId
        )
        depositRepository.save(deposit)

        sendNotification(request.sagaId, request.accountNumber, request.amount, request.fromAccountNumber)

        return DepositResponse(deposit.depositId, "COMPLETED")
    }

    /** Notification Service에 REST 호출로 알림을 전송한다 (best-effort). */
    private fun sendNotification(sagaId: String, accountNumber: String, amount: BigDecimal, fromAccountNumber: String) {
        try {
            val notificationRequest = NotificationRequest(
                sagaId = sagaId,
                userId = accountNumber,
                notificationType = "DEPOSIT_SUCCESS",
                message = "Received $amount from $fromAccountNumber"
            )

            restTemplate.postForObject(
                "$notificationServiceUrl/internal/notification",
                notificationRequest,
                NotificationResponse::class.java
            )
        } catch (e: Exception) {
            println("[ORCHESTRATION] Failed to send notification for saga $sagaId: ${e.message}")
        }
    }
}
