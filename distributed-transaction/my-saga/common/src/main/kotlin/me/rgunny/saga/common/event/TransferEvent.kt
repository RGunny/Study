package me.rgunny.saga.common.event

import java.math.BigDecimal

// ===== Withdraw =====
data class WithdrawSuccessEvent(
    val sagaId: String,
    val accountNumber: String,
    val toAccountNumber: String,
    val amount: BigDecimal
)

data class WithdrawFailedEvent(
    val sagaId: String,
    val accountNumber: String,
    val reason: String
)

// ===== Deposit =====
data class DepositSuccessEvent(
    val sagaId: String,
    val accountNumber: String,
    val amount: BigDecimal
)

data class DepositFailedEvent(
    val sagaId: String,
    val accountNumber: String,
    val reason: String,
    val amount: BigDecimal,
    val fromAccountNumber: String
)

// ===== Notification =====
data class NotificationSuccessEvent(
    val sagaId: String,
    val accountNumber: String,
)

data class NotificationFailedEvent(
    val sagaId: String,
    val accountNumber: String,
    val reason: String
)
