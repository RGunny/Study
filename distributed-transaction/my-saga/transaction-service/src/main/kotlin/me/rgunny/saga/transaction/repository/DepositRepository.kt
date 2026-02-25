package me.rgunny.saga.transaction.repository

import me.rgunny.saga.transaction.domain.Deposit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DepositRepository : JpaRepository<Deposit, String> {
    fun findBySagaId(sagaId: String): Deposit?
}
