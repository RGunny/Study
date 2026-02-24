package me.rgunny.saga.coordinator.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "saga_state")
class SagaState(

    @Id
    @Column(name = "saga_id")
    val sagaId: String,

    @Column(name = "from_account_number", nullable = false)
    val fromAccountNumber: String,

    @Column(name = "to_account_number", nullable = false)
    val toAccountNumber: String,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    var status: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
