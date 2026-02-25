package me.rgunny.saga.transaction.service

import me.rgunny.saga.transaction.domain.Deposit
import me.rgunny.saga.transaction.repository.DepositRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

/**
 * TCC (Try-Confirm-Cancel) 참여자 서비스 (transaction-service 측).
 *
 * - tryReserve: PENDING 상태의 deposit 생성 (예약)
 * - confirm:    PENDING → CONFIRMED (확정)
 * - cancel:     PENDING → CANCELLED (취소)
 */
@Service
class TccDepositService(
    private val depositRepository: DepositRepository
) {

    /** TCC TRY: PENDING 상태의 deposit 생성 */
    @Transactional
    fun tryReserve(sagaId: String, accountNumber: String, amount: BigDecimal): Boolean {
        val deposit = Deposit(
            depositId = UUID.randomUUID().toString(),
            transactionId = sagaId,
            accountNumber = accountNumber,
            amount = amount,
            status = "PENDING",
            sagaId = sagaId
        )
        depositRepository.save(deposit)
        return true
    }

    /** TCC CONFIRM: PENDING → CONFIRMED */
    @Transactional
    fun confirm(sagaId: String): Boolean {
        val deposit = depositRepository.findBySagaId(sagaId) ?: return false
        if (deposit.status != "PENDING") return false
        deposit.status = "CONFIRMED"
        depositRepository.save(deposit)
        return true
    }

    /** TCC CANCEL: PENDING → CANCELLED */
    @Transactional
    fun cancel(sagaId: String): Boolean {
        val deposit = depositRepository.findBySagaId(sagaId) ?: return false
        if (deposit.status != "PENDING") return false
        deposit.status = "CANCELLED"
        depositRepository.save(deposit)
        return true
    }
}
