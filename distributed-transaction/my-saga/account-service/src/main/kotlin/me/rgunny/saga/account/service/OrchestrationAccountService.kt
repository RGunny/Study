package me.rgunny.saga.account.service

import me.rgunny.saga.account.domain.AccountTransaction
import me.rgunny.saga.account.repository.AccountRepository
import me.rgunny.saga.account.repository.AccountTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

/**
 * Orchestration Saga 참여자 서비스 (account-service 측).
 *
 * saga-coordinator가 REST 호출로 출금/보상을 요청할 때 사용된다.
 */
@Service
class OrchestrationAccountService(
    private val accountRepository: AccountRepository,
    private val accountTransactionRepository: AccountTransactionRepository
) {

    /**
     * 잔액 검증 후 출금을 처리한다.
     *
     * @return 출금 성공 시 WithdrawResult, 잔액 부족 시 null
     * @throws RuntimeException 계좌를 찾을 수 없는 경우
     */
    @Transactional
    fun withdraw(sagaId: String, accountNumber: String, amount: BigDecimal): WithdrawResult? {
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

    /**
     * 출금을 보상 처리한다: 잔액 복원.
     *
     * @throws RuntimeException 계좌를 찾을 수 없는 경우
     */
    @Transactional
    fun compensateWithdraw(accountNumber: String, amount: BigDecimal) {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw RuntimeException("Account not found for compensation")

        account.balance = account.balance.add(amount)
        accountRepository.save(account)
    }
}

data class WithdrawResult(val accountId: String, val transactionId: String)
