package me.rgunny.saga.account.repository

import me.rgunny.saga.account.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, String> {

    fun findByAccountNumber(accountNumber: String): Account?
}