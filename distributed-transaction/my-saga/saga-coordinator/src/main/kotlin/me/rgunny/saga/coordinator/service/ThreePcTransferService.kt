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
 * Three-Phase Commit (3PC) 코디네이터.
 *
 * 2PC에 CAN_COMMIT 단계를 추가하여 blocking 문제를 완화한다.
 *
 * Phase 1 (CAN_COMMIT): 참여자에게 참여 가능 여부만 확인 (리소스 잠금 없음)
 * Phase 2 (PRE_COMMIT):  2PC의 PREPARE와 동일 — 리소스 잠금 + PREPARED 상태 생성
 * Phase 3 (DO_COMMIT):   2PC의 COMMIT과 동일 — 확정
 *
 * 핵심 차이: 코디네이터 장애 시 PRE_COMMIT 이후 참여자는 timeout 후 자동 커밋 가능.
 * (실습에서는 timeout 로직을 구현하지 않고, 흐름 차이만 보여준다.)
 */
@Service
class ThreePcTransferService(
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
            sagaType = "THREE_PC",
            status = "CAN_COMMIT"
        )
        sagaStateRepository.save(sagaState)

        // ── Phase 1: CAN_COMMIT ──
        val accountCanCommit = askCanCommit(
            "$accountServiceUrl/internal/3pc/can-commit",
            CanCommitRequest(request.fromAccountNumber, request.amount)
        )
        val transactionCanCommit = askCanCommit(
            "$transactionServiceUrl/internal/3pc/can-commit",
            CanCommitRequest(request.toAccountNumber, request.amount)
        )

        if (!accountCanCommit || !transactionCanCommit) {
            sagaState.status = "ABORTED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "CAN_COMMIT rejected by participant")
        }

        // ── Phase 2: PRE_COMMIT (= 2PC PREPARE 재사용) ──
        sagaState.status = "PRE_COMMITTING"
        sagaStateRepository.save(sagaState)

        val accountPrepared = prepareParticipant(
            "$accountServiceUrl/internal/2pc/prepare",
            PrepareRequest(sagaId, request.fromAccountNumber, request.amount)
        )

        if (!accountPrepared) {
            sagaState.status = "ABORTED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Account pre-commit failed")
        }

        val transactionPrepared = prepareParticipant(
            "$transactionServiceUrl/internal/2pc/prepare",
            PrepareRequest(sagaId, request.toAccountNumber, request.amount)
        )

        if (!transactionPrepared) {
            rollbackParticipant("$accountServiceUrl/internal/2pc/rollback", CommitRequest(sagaId))
            sagaState.status = "ABORTED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Transaction pre-commit failed, account rolled back")
        }

        // ── Phase 3: DO_COMMIT (= 2PC COMMIT 재사용) ──
        // 실제 3PC에서는 이 시점에서 코디네이터 장애가 발생하면
        // 참여자가 timeout 후 자동 커밋한다 (PRE_COMMIT 완료 = 커밋 확정 의미).
        sagaState.status = "DO_COMMITTING"
        sagaStateRepository.save(sagaState)

        commitParticipant("$accountServiceUrl/internal/2pc/commit", CommitRequest(sagaId))
        commitParticipant("$transactionServiceUrl/internal/2pc/commit", CommitRequest(sagaId))

        sagaState.status = "COMMITTED"
        sagaStateRepository.save(sagaState)
        return TransferResponse(sagaId, "COMMITTED", "3PC transfer successful")
    }

    private fun askCanCommit(url: String, request: CanCommitRequest): Boolean {
        return try {
            val response = restTemplate.postForObject(url, request, CanCommitResponse::class.java)
            response?.vote == true
        } catch (e: Exception) {
            false
        }
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
            println("[3PC] Commit failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }

    private fun rollbackParticipant(url: String, request: CommitRequest) {
        try {
            restTemplate.postForObject(url, request, CommitResponse::class.java)
        } catch (e: Exception) {
            println("[3PC] Rollback failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }
}
