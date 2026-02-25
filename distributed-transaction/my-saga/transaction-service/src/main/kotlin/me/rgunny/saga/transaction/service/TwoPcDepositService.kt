package me.rgunny.saga.transaction.service

import me.rgunny.saga.transaction.domain.Deposit
import me.rgunny.saga.transaction.repository.DepositRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

/**
 * 2PC / 3PC 참여자 서비스 (transaction-service 측).
 *
 * - prepare:  PREPARED 상태의 deposit 레코드 생성
 * - commit:   PREPARED → COMMITTED
 * - rollback: PREPARED → ABORTED
 * - canCommit: 입금 참여 가능 여부 응답 (3PC Phase 1)
 */
@Service
class TwoPcDepositService(
    private val depositRepository: DepositRepository
) {

    /** 2PC PREPARE: PREPARED 상태의 deposit 레코드 생성 */
    @Transactional
    fun prepare(sagaId: String, accountNumber: String, amount: BigDecimal): Boolean {
        val deposit = Deposit(
            depositId = UUID.randomUUID().toString(),
            transactionId = sagaId,
            accountNumber = accountNumber,
            amount = amount,
            status = "PREPARED",
            sagaId = sagaId
        )
        depositRepository.save(deposit)
        return true
    }

    /** 2PC COMMIT: PREPARED → COMMITTED */
    @Transactional
    fun commitPrepared(sagaId: String): Boolean {
        val deposit = depositRepository.findBySagaId(sagaId) ?: return false
        if (deposit.status != "PREPARED") return false
        deposit.status = "COMMITTED"
        depositRepository.save(deposit)
        return true
    }

    /** 2PC ROLLBACK: PREPARED → ABORTED */
    @Transactional
    fun rollbackPrepared(sagaId: String): Boolean {
        val deposit = depositRepository.findBySagaId(sagaId) ?: return false
        if (deposit.status != "PREPARED") return false
        deposit.status = "ABORTED"
        depositRepository.save(deposit)
        return true
    }

    /** 3PC CAN_COMMIT: 입금 참여 가능 여부 (항상 가능) */
    fun canCommit(accountNumber: String, amount: BigDecimal): Boolean {
        return true
    }
}
