package me.rgunny.saga.transaction.service

import me.rgunny.saga.transaction.domain.Deposit
import me.rgunny.saga.transaction.domain.Transaction
import me.rgunny.saga.transaction.repository.DepositRepository
import me.rgunny.saga.transaction.repository.TransactionRepository
import me.rgunny.saga.common.event.DepositFailedEvent
import me.rgunny.saga.common.event.DepositSuccessEvent
import me.rgunny.saga.common.event.WithdrawSuccessEvent
import me.rgunny.saga.common.event.NotificationFailedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Choreography Saga 참여자 서비스 (transaction-service 측).
 *
 * Kafka 이벤트를 구독하여 입금을 처리하고 결과 이벤트를 발행한다.
 */
@Service
class ChoreographyDepositService(
    private val transactionRepository: TransactionRepository,
    private val depositRepository: DepositRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    /** 출금 성공 이벤트를 수신하여 입금을 처리하고 결과 이벤트를 발행한다. */
    @KafkaListener(topics = ["account.withdraw.success"], groupId = "transaction-service-group")
    @Transactional
    fun handleWithdrawSuccess(event: WithdrawSuccessEvent) {
        try {
            val transactionId = UUID.randomUUID().toString()
            val depositId = UUID.randomUUID().toString()

            val transaction = Transaction(
                transactionId = transactionId,
                sagaId = event.sagaId,
                fromAccountNumber = event.accountNumber,
                toAccountNumber = event.toAccountNumber,
                amount = event.amount,
                status = "COMPLETED"
            )
            transactionRepository.save(transaction)

            val deposit = Deposit(
                depositId = depositId,
                transactionId = transactionId,
                accountNumber = event.toAccountNumber,
                amount = event.amount,
                status = "COMPLETED",
                sagaId = event.sagaId
            )
            depositRepository.save(deposit)

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
        println("[CHOREOGRAPHY] Notification failed for saga ${event.sagaId}: ${event.reason}")
    }
}
