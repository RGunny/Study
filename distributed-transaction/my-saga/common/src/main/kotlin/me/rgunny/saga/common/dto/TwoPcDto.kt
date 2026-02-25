package me.rgunny.saga.common.dto

import java.math.BigDecimal

data class PrepareRequest(
    val sagaId: String,
    val accountNumber: String,
    val amount: BigDecimal
)

data class PrepareResponse(
    val sagaId: String,
    val status: String
)

data class CommitRequest(
    val sagaId: String
)

data class CommitResponse(
    val sagaId: String,
    val status: String
)

data class CanCommitRequest(
    val accountNumber: String,
    val amount: BigDecimal
)

data class CanCommitResponse(
    val vote: Boolean
)
