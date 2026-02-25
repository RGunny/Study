package me.rgunny.saga.coordinator.service

import me.rgunny.saga.coordinator.domain.SagaState
import me.rgunny.saga.coordinator.repository.SagaStateRepository
import me.rgunny.saga.common.dto.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.UUID

/**
 * Two-Phase Commit (2PC) 코디네이터.
 *
 * Phase 1 (PREPARE): 모든 참여자에게 prepare 요청
 * Phase 2 (COMMIT/ROLLBACK): 모두 PREPARED이면 commit, 하나라도 실패하면 rollback
 */
@Service
class TwoPcTransferService(
    private val sagaStateRepository: SagaStateRepository,
    private val restTemplate: RestTemplate,
    @Value("\${service.account.url}") private val accountServiceUrl: String,
    @Value("\${service.transaction.url}") private val transactionServiceUrl: String
) {

    @Transactional
    fun executeTransfer(request: TransferRequest): TransferResponse {
        val sagaId = UUID.randomUUID().toString()

        val sagaState = SagaState(
            sagaId = sagaId,
            fromAccountNumber = request.fromAccountNumber,
            toAccountNumber = request.toAccountNumber,
            amount = request.amount,
            sagaType = "TWO_PC",
            status = "PREPARING"
        )
        sagaStateRepository.save(sagaState)

        // ── Phase 1: PREPARE ──
        val accountPrepared = prepareParticipant(
            "$accountServiceUrl/internal/2pc/prepare",
            PrepareRequest(sagaId, request.fromAccountNumber, request.amount)
        )

        if (!accountPrepared) {
            sagaState.status = "ABORTED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Account prepare failed (insufficient balance)")
        }

        val transactionPrepared = prepareParticipant(
            "$transactionServiceUrl/internal/2pc/prepare",
            PrepareRequest(sagaId, request.toAccountNumber, request.amount)
        )

        if (!transactionPrepared) {
            // account는 prepared 상태이므로 rollback 필요
            rollbackParticipant("$accountServiceUrl/internal/2pc/rollback", CommitRequest(sagaId))
            sagaState.status = "ABORTED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Transaction prepare failed, account rolled back")
        }

        // ── Phase 2: COMMIT ──
        sagaState.status = "COMMITTING"
        sagaStateRepository.save(sagaState)

        commitParticipant("$accountServiceUrl/internal/2pc/commit", CommitRequest(sagaId))
        commitParticipant("$transactionServiceUrl/internal/2pc/commit", CommitRequest(sagaId))

        sagaState.status = "COMMITTED"
        sagaStateRepository.save(sagaState)
        return TransferResponse(sagaId, "COMMITTED", "2PC transfer successful")
    }

    private fun prepareParticipant(url: String, request: PrepareRequest): Boolean {
        return try {
            val response = restTemplate.postForObject(url, request, PrepareResponse::class.java)
            response?.status == "PREPARED"
        } catch (e: Exception) {
            false
        }
    }

    private fun commitParticipant(url: String, request: CommitRequest) {
        try {
            restTemplate.postForObject(url, request, CommitResponse::class.java)
        } catch (e: Exception) {
            println("[2PC] Commit failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }

    private fun rollbackParticipant(url: String, request: CommitRequest) {
        try {
            restTemplate.postForObject(url, request, CommitResponse::class.java)
        } catch (e: Exception) {
            println("[2PC] Rollback failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }
}
