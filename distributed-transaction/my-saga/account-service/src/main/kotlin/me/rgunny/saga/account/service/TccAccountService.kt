package me.rgunny.saga.account.service

import me.rgunny.saga.account.domain.AccountTransaction
import me.rgunny.saga.account.repository.AccountRepository
import me.rgunny.saga.account.repository.AccountTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

/**
 * TCC (Try-Confirm-Cancel) 참여자 서비스 (account-service 측).
 *
 * - try: balance 차감 + frozenAmount 증가 (소프트 잠금)
 * - confirm: frozenAmount 차감 (확정)
 * - cancel: frozenAmount → balance 복원 (취소)
 */
@Service
class TccAccountService(
    private val accountRepository: AccountRepository,
    private val accountTransactionRepository: AccountTransactionRepository
) {

    /** TCC TRY: balance 차감 + frozenAmount 증가 (예약) */
    @Transactional
    fun tryReserve(sagaId: String, accountNumber: String, amount: BigDecimal): Boolean {
        val account = accountRepository.findByAccountNumber(accountNumber) ?: return false
        if (account.balance < amount) return false

        account.balance = account.balance.subtract(amount)
        account.frozenAmount = account.frozenAmount.add(amount)
        accountRepository.save(account)

        val tx = AccountTransaction(
            transactionId = UUID.randomUUID().toString(),
            accountId = account.accountId,
            amount = amount,
            transactionType = "WITHDRAW",
            sagaId = sagaId,
            status = "TRYING"
        )
        accountTransactionRepository.save(tx)
        return true
    }

    /** TCC CONFIRM: frozenAmount 차감 (확정) */
    @Transactional
    fun confirm(sagaId: String): Boolean {
        val tx = accountTransactionRepository.findBySagaId(sagaId) ?: return false
        if (tx.status != "TRYING") return false

        val account = accountRepository.findById(tx.accountId).orElse(null) ?: return false
        account.frozenAmount = account.frozenAmount.subtract(tx.amount)
        accountRepository.save(account)

        tx.status = "CONFIRMED"
        accountTransactionRepository.save(tx)
        return true
    }

    /** TCC CANCEL: frozenAmount → balance 복원 */
    @Transactional
    fun cancel(sagaId: String): Boolean {
        val tx = accountTransactionRepository.findBySagaId(sagaId) ?: return false
        if (tx.status != "TRYING") return false

        val account = accountRepository.findById(tx.accountId).orElse(null) ?: return false
        account.frozenAmount = account.frozenAmount.subtract(tx.amount)
        account.balance = account.balance.add(tx.amount)
        accountRepository.save(account)

        tx.status = "CANCELLED"
        accountTransactionRepository.save(tx)
        return true
    }
}
