package me.rgunny.saga.transaction.repository

import me.rgunny.saga.transaction.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, String>
