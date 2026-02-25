package me.rgunny.saga.common.dto

import java.math.BigDecimal

data class TccTryRequest(
    val sagaId: String,
    val accountNumber: String,
    val amount: BigDecimal
)

data class TccTryResponse(
    val sagaId: String,
    val status: String
)

data class TccConfirmRequest(
    val sagaId: String
)

data class TccConfirmResponse(
    val sagaId: String,
    val status: String
)
