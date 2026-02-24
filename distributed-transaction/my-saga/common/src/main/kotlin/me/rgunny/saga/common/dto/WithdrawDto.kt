package me.rgunny.saga.common.dto

import java.math.BigDecimal

data class WithdrawRequest(
    val sagaId: String,
    val accountNumber: String,
    val amount: BigDecimal
)

data class WithdrawResponse(
    val transactionId: String,
    val status: String
)
