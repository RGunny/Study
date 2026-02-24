package com.saga.transaction.service


import com.saga.transaction.domain.Deposit
import com.saga.transaction.domain.Transaction
import com.saga.transaction.repository.DepositRepository
import com.saga.transaction.repository.TransactionRepository
import com.saga.common.dto.DepositRequest
import com.saga.common.dto.DepositResponse
import com.saga.common.dto.NotificationRequest
import com.saga.common.dto.NotificationResponse
import com.saga.common.event.DepositFailedEvent
import com.saga.common.event.DepositSuccessEvent
import com.saga.common.event.WithdrawSuccessEvent
import com.saga.common.event.NotificationFailedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class DepositService(
    private val transactionRepository: TransactionRepository,
    private val depositRepository: DepositRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val restTemplate: RestTemplate,
    @Value("\${service.notification.url}") private val notificationServiceUrl: String
) {
    @Transactional
    fun processDeposit(request: DepositRequest): DepositResponse {
        try {
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


            try {
                val notificationRequest = NotificationRequest(
                    sagaId = request.sagaId,
                    userId = request.accountNumber,
                    notificationType = "DEPOSIT_SUCCESS",
                    message = "Received ${request.amount} from ${request.fromAccountNumber}"
                )

                restTemplate.postForObject(
                    "$notificationServiceUrl/internal/notification",
                    notificationRequest,
                    NotificationResponse::class.java
                )
            } catch (e : Exception) {

            }

            return DepositResponse(depositId, "COMPLETED")

        } catch (e : Exception) {
            throw RuntimeException("Exception occurred while processing deposit", e)
        }
    }

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

            val depositSuccessEvent = DepositSuccessEvent(
                sagaId = event.sagaId,
                accountNumber = event.toAccountNumber,
                amount = event.amount
            )
            kafkaTemplate.send("transaction.deposit.success", depositSuccessEvent)

        } catch (e : Exception) {
            // DLQ 패턴을 통해 보상 트랜잭션을 따로 구현해도 무방하다.
            val depositFailedEvent = DepositFailedEvent(
                sagaId = event.sagaId,
                accountNumber = event.toAccountNumber,
                reason = e.message ?: "Unknown error"
            )
            kafkaTemplate.send("transaction.deposit.failed", depositFailedEvent)
        }
    }

    @KafkaListener(topics = ["notification.failed"], groupId = "transaction-service-group")
    fun handleNotificationFailed(event: NotificationFailedEvent) {
        println("[TRANSACTION] Notification failed for saga ${event.sagaId}: ${event.reason}")
    }

}