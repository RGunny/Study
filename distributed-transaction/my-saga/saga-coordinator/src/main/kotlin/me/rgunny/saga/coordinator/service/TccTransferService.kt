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
 * TCC (Try-Confirm-Cancel) 코디네이터.
 *
 * Try Phase:    각 참여자에게 리소스 예약 요청
 * Confirm Phase: 모두 성공 시 예약 확정
 * Cancel Phase:  하나라도 실패 시 예약 취소
 *
 * 2PC/3PC와의 핵심 차이:
 * - 비즈니스 로직 수준에서 예약/확정/취소를 구현 (인프라 의존 없음)
 * - Try 단계에서 frozen_amount로 리소스를 "소프트 잠금"
 */
@Service
class TccTransferService(
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
            sagaType = "TCC",
            status = "TRYING"
        )
        sagaStateRepository.save(sagaState)

        // ── Try Phase ──
        val accountTried = tryParticipant(
            "$accountServiceUrl/internal/tcc/try",
            TccTryRequest(sagaId, request.fromAccountNumber, request.amount)
        )

        if (!accountTried) {
            sagaState.status = "CANCELLED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Account try failed (insufficient balance)")
        }

        val transactionTried = tryParticipant(
            "$transactionServiceUrl/internal/tcc/try",
            TccTryRequest(sagaId, request.toAccountNumber, request.amount)
        )

        if (!transactionTried) {
            // account는 try 상태이므로 cancel 필요
            cancelParticipant("$accountServiceUrl/internal/tcc/cancel", TccConfirmRequest(sagaId))
            sagaState.status = "CANCELLED"
            sagaStateRepository.save(sagaState)
            return TransferResponse(sagaId, "FAILED", "Transaction try failed, account cancelled")
        }

        // ── Confirm Phase ──
        sagaState.status = "CONFIRMING"
        sagaStateRepository.save(sagaState)

        confirmParticipant("$accountServiceUrl/internal/tcc/confirm", TccConfirmRequest(sagaId))
        confirmParticipant("$transactionServiceUrl/internal/tcc/confirm", TccConfirmRequest(sagaId))

        sagaState.status = "CONFIRMED"
        sagaStateRepository.save(sagaState)
        return TransferResponse(sagaId, "CONFIRMED", "TCC transfer successful")
    }

    private fun tryParticipant(url: String, request: TccTryRequest): Boolean {
        return try {
            val response = restTemplate.postForObject(url, request, TccTryResponse::class.java)
            response?.status == "RESERVED"
        } catch (e: Exception) {
            false
        }
    }

    private fun confirmParticipant(url: String, request: TccConfirmRequest) {
        try {
            restTemplate.postForObject(url, request, TccConfirmResponse::class.java)
        } catch (e: Exception) {
            println("[TCC] Confirm failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }

    private fun cancelParticipant(url: String, request: TccConfirmRequest) {
        try {
            restTemplate.postForObject(url, request, TccConfirmResponse::class.java)
        } catch (e: Exception) {
            println("[TCC] Cancel failed at $url for saga ${request.sagaId}: ${e.message}")
        }
    }
}
