package me.rgunny.saga.account.repository

import me.rgunny.saga.account.domain.AccountTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountTransactionRepository : JpaRepository<AccountTransaction, String> {
    fun findBySagaId(sagaId: String): AccountTransaction?
}