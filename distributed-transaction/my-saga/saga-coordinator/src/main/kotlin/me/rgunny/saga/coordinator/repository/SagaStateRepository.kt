package me.rgunny.saga.coordinator.repository

import me.rgunny.saga.coordinator.domain.SagaState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SagaStateRepository : JpaRepository<SagaState, String>
