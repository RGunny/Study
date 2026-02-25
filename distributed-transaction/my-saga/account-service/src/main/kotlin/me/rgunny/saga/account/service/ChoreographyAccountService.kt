package me.rgunny.saga.account.service

import me.rgunny.saga.account.domain.AccountTransaction
import me.rgunny.saga.account.repository.AccountRepository
import me.rgunny.saga.account.repository.AccountTransactionRepository
import me.rgunny.saga.common.dto.TransferRequest
import me.rgunny.saga.common.dto.TransferResponse
import me.rgunny.saga.common.event.DepositFailedEvent
import me.rgunny.saga.common.event.DepositSuccessEvent
import me.rgunny.saga.common.event.NotificationFailedEvent
import me.rgunny.saga.common.event.WithdrawFailedEvent
import me.rgunny.saga.common.event.WithdrawSuccessEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/**
 * Choreography Saga 참여자 서비스 (account-service 측).
 *
 * 중앙 오케스트레이터 없이, 각 서비스가 이벤트를 발행/구독하여 Saga를 진행한다.
 * SagaState 없이 이벤트에 담긴 정보만으로 보상을 처리한다.
 *
 * 호출 흐름:
 * 1. 출금 처리 + WithdrawSuccessEvent 발행
 * 2. Transaction Service가 이벤트를 구독하여 입금 처리
 * 3. 입금 결과 이벤트를 구독하여 보상 처리 (실패 시)
 */
@Service
class ChoreographyAccountService(
    private val accountRepository: AccountRepository,
    private val accountTransactionRepository: AccountTransactionRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {

    /**
     * Choreography Saga로 이체를 시작한다.
     *
     * 출금 → 출금 성공 이벤트 발행 순서로 진행한다.
     * 이후 입금은 Transaction Service가 이벤트를 구독하여 비동기로 처리한다.
     */
    @Transactional
    fun initiateTransfer(request: TransferRequest): TransferResponse {
        val sagaId = UUID.randomUUID().toString()

        // Step 1: 출금
        val result = withdraw(sagaId, request.fromAccountNumber, request.amount)
            ?: return handleWithdrawFailed(sagaId, request.fromAccountNumber, "Insufficient balance")

        // Step 2: 출금 성공 이벤트 발행 -> Transaction Service가 구독하여 입금 처리
        publishWithdrawEvent(sagaId, request)
        return TransferResponse(sagaId, "STARTED", "Transfer initiated")
    }

    /** 잔액 검증 후 출금을 처리한다. */
    private fun withdraw(sagaId: String, accountNumber: String, amount: BigDecimal): WithdrawResult? {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw RuntimeException("Source account not found")

        if (account.balance < amount) {
            return null
        }

        account.balance = account.balance.subtract(amount)
        accountRepository.save(account)

        val withdrawTx = AccountTransaction(
            transactionId = UUID.randomUUID().toString(),
            accountId = account.accountId,
            amount = amount,
            transactionType = "WITHDRAW",
            sagaId = sagaId,
            status = "COMPLETED"
        )
        accountTransactionRepository.save(withdrawTx)

        return WithdrawResult(account.accountId, withdrawTx.transactionId)
    }

    /** 출금을 보상 처리한다: 잔액 복원. */
    private fun compensateWithdraw(accountNumber: String, amount: BigDecimal) {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw RuntimeException("Account not found for compensation")

        account.balance = account.balance.add(amount)
        accountRepository.save(account)
    }

    /** WithdrawSuccessEvent를 Kafka로 발행한다. */
    private fun publishWithdrawEvent(sagaId: String, request: TransferRequest) {
        val event = WithdrawSuccessEvent(
            sagaId = sagaId,
            accountNumber = request.fromAccountNumber,
            toAccountNumber = request.toAccountNumber,
            amount = request.amount
        )
        kafkaTemplate.send("account.withdraw.success", event)
    }

    /** 출금 실패 이벤트를 발행하고 실패 응답을 반환한다. */
    private fun handleWithdrawFailed(sagaId: String, accountNumber: String, reason: String): TransferResponse {
        val event = WithdrawFailedEvent(sagaId, accountNumber, reason)
        kafkaTemplate.send("account.withdraw.failed", event)
        return TransferResponse(sagaId, "FAILED", reason)
    }

    /** 입금 성공 이벤트를 수신한다. 순수 Choreography에서는 암묵적으로 Saga가 완료된다. */
    @KafkaListener(topics = ["transaction.deposit.success"], groupId = "account-service-group")
    fun handleDepositSuccess(event: DepositSuccessEvent) {
        println("[CHOREOGRAPHY] Saga ${event.sagaId} completed: deposit of ${event.amount} to ${event.accountNumber}")
    }

    /** 입금 실패 이벤트를 수신하여 이벤트에 담긴 정보만으로 출금을 보상한다. */
    @KafkaListener(topics = ["transaction.deposit.failed"], groupId = "account-service-group")
    @Transactional
    fun handleDepositFailed(event: DepositFailedEvent) {
        compensateWithdraw(event.fromAccountNumber, event.amount)
        println("[CHOREOGRAPHY] Saga ${event.sagaId} compensated: ${event.reason}")
    }

    /** 알림 실패 이벤트를 수신한다. 이체 자체는 성공이므로 로그만 남긴다. */
    @KafkaListener(topics = ["notification.failed"], groupId = "account-service-group")
    fun handleNotificationFailed(event: NotificationFailedEvent) {
        println("[CHOREOGRAPHY] Saga ${event.sagaId} notification failed: ${event.reason}")
    }
}
