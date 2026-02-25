package me.rgunny.saga.account.service

import me.rgunny.saga.account.domain.AccountTransaction
import me.rgunny.saga.account.repository.AccountRepository
import me.rgunny.saga.account.repository.AccountTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

/**
 * 2PC / 3PC 참여자 서비스 (account-service 측).
 *
 * - prepare: 잔액 검증 + 차감 + PREPARED 트랜잭션 생성
 * - commit:  PREPARED → COMMITTED
 * - rollback: PREPARED → ABORTED + 잔액 복원
 * - canCommit: 리소스 잠금 없이 참여 가능 여부만 응답 (3PC Phase 1)
 */
@Service
class TwoPcAccountService(
    private val accountRepository: AccountRepository,
    private val accountTransactionRepository: AccountTransactionRepository
) {

    /** 2PC PREPARE: 잔액 검증 + 잔액 차감 + PREPARED 상태 트랜잭션 생성 */
    @Transactional
    fun prepare(sagaId: String, accountNumber: String, amount: BigDecimal): Boolean {
        val account = accountRepository.findByAccountNumber(accountNumber) ?: return false
        if (account.balance < amount) return false

        account.balance = account.balance.subtract(amount)
        accountRepository.save(account)

        val tx = AccountTransaction(
            transactionId = UUID.randomUUID().toString(),
            accountId = account.accountId,
            amount = amount,
            transactionType = "WITHDRAW",
            sagaId = sagaId,
            status = "PREPARED"
        )
        accountTransactionRepository.save(tx)
        return true
    }

    /** 2PC COMMIT: PREPARED → COMMITTED */
    @Transactional
    fun commitPrepared(sagaId: String): Boolean {
        val tx = accountTransactionRepository.findBySagaId(sagaId) ?: return false
        if (tx.status != "PREPARED") return false
        tx.status = "COMMITTED"
        accountTransactionRepository.save(tx)
        return true
    }

    /** 2PC ROLLBACK: PREPARED → ABORTED + 잔액 복원 */
    @Transactional
    fun rollbackPrepared(sagaId: String): Boolean {
        val tx = accountTransactionRepository.findBySagaId(sagaId) ?: return false
        if (tx.status != "PREPARED") return false

        val account = accountRepository.findById(tx.accountId).orElse(null) ?: return false
        account.balance = account.balance.add(tx.amount)
        accountRepository.save(account)

        tx.status = "ABORTED"
        accountTransactionRepository.save(tx)
        return true
    }

    /** 3PC CAN_COMMIT: 리소스 잠금 없이 참여 가능 여부만 확인 */
    fun canCommit(accountNumber: String, amount: BigDecimal): Boolean {
        val account = accountRepository.findByAccountNumber(accountNumber) ?: return false
        return account.balance >= amount
    }
}
