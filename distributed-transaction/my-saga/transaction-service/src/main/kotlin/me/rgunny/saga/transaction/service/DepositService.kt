package me.rgunny.saga.transaction.service

import me.rgunny.saga.transaction.domain.Deposit
import me.rgunny.saga.transaction.domain.Transaction
import me.rgunny.saga.transaction.repository.DepositRepository
import me.rgunny.saga.transaction.repository.TransactionRepository
import me.rgunny.saga.common.dto.DepositRequest
import me.rgunny.saga.common.dto.DepositResponse
import me.rgunny.saga.common.dto.NotificationRequest
import me.rgunny.saga.common.dto.NotificationResponse
import me.rgunny.saga.common.event.DepositFailedEvent
import me.rgunny.saga.common.event.DepositSuccessEvent
import me.rgunny.saga.common.event.WithdrawSuccessEvent
import me.rgunny.saga.common.event.NotificationFailedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.util.*

/**
 * 입금 처리를 담당하는 서비스.
 *
 * Orchestration 패턴에서는 REST 호출로, Choreography 패턴에서는 Kafka 이벤트 구독으로 입금을 처리한다.
 *
 * 호출 흐름:
 * 1. (Orchestration) Account Service → REST → processDeposit → Notification Service
 * 2. (Choreography) WithdrawSuccessEvent 구독 → handleWithdrawSuccess → DepositSuccessEvent 발행
 */
@Service
class DepositService(
    private val transactionRepository: TransactionRepository,
    private val depositRepository: DepositRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val restTemplate: RestTemplate,
    @Value("\${service.notification.url}") private val notificationServiceUrl: String
) {
    /** Orchestration 패턴에서 REST 호출로 입금을 처리한다. */
    @Transactional
    fun processDeposit(request: DepositRequest): DepositResponse {
        val (_, deposit) = createTransactionAndDeposit(
            sagaId = request.sagaId,
            fromAccountNumber = request.fromAccountNumber,
            toAccountNumber = request.accountNumber,
            amount = request.amount
        )

        sendNotification(request.sagaId, request.accountNumber, request.amount, request.fromAccountNumber)

        return DepositResponse(deposit.depositId, "COMPLETED")
    }

    /** 출금 성공 이벤트를 수신하여 입금을 처리하고 결과 이벤트를 발행한다. */
    @KafkaListener(topics = ["account.withdraw.success"], groupId = "transaction-service-group")
    @Transactional
    fun handleWithdrawSuccess(event: WithdrawSuccessEvent) {
        try {
            createTransactionAndDeposit(
                sagaId = event.sagaId,
                fromAccountNumber = event.accountNumber,
                toAccountNumber = event.toAccountNumber,
                amount = event.amount
            )

            val successEvent = DepositSuccessEvent(event.sagaId, event.toAccountNumber, event.amount)
            kafkaTemplate.send("transaction.deposit.success", successEvent)
        } catch (e: Exception) {
            val failedEvent = DepositFailedEvent(
                sagaId = event.sagaId,
                accountNumber = event.toAccountNumber,
                reason = e.message ?: "Unknown error",
                amount = event.amount,
                fromAccountNumber = event.accountNumber
            )
            kafkaTemplate.send("transaction.deposit.failed", failedEvent)
        }
    }

    /** 알림 실패 이벤트를 수신하여 로그를 출력한다. */
    @KafkaListener(topics = ["notification.failed"], groupId = "transaction-service-group")
    fun handleNotificationFailed(event: NotificationFailedEvent) {
        println("[TRANSACTION] Notification failed for saga ${event.sagaId}: ${event.reason}")
    }

    /** Transaction + Deposit 엔티티를 생성하고 저장한다. */
    private fun createTransactionAndDeposit(
        sagaId: String,
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: BigDecimal
    ): Pair<Transaction, Deposit> {
        val transactionId = UUID.randomUUID().toString()
        val depositId = UUID.randomUUID().toString()

        val transaction = Transaction(
            transactionId = transactionId,
            sagaId = sagaId,
            fromAccountNumber = fromAccountNumber,
            toAccountNumber = toAccountNumber,
            amount = amount,
            status = "COMPLETED"
        )
        transactionRepository.save(transaction)

        val deposit = Deposit(
            depositId = depositId,
            transactionId = transactionId,
            accountNumber = toAccountNumber,
            amount = amount,
            status = "COMPLETED",
            sagaId = sagaId
        )
        depositRepository.save(deposit)

        return Pair(transaction, deposit)
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
            println("[TRANSACTION] Failed to send notification for saga $sagaId: ${e.message}")
        }
    }
}
