package me.rgunny.saga.transaction.api

import me.rgunny.saga.transaction.service.TccDepositService
import me.rgunny.saga.common.dto.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TccController(
    private val tccDepositService: TccDepositService
) {

    @PostMapping("/internal/tcc/try")
    fun tryReserve(@RequestBody request: TccTryRequest): TccTryResponse {
        val success = tccDepositService.tryReserve(request.sagaId, request.accountNumber, request.amount)
        return TccTryResponse(request.sagaId, if (success) "RESERVED" else "FAILED")
    }

    @PostMapping("/internal/tcc/confirm")
    fun confirm(@RequestBody request: TccConfirmRequest): TccConfirmResponse {
        val success = tccDepositService.confirm(request.sagaId)
        return TccConfirmResponse(request.sagaId, if (success) "CONFIRMED" else "FAILED")
    }

    @PostMapping("/internal/tcc/cancel")
    fun cancel(@RequestBody request: TccConfirmRequest): TccConfirmResponse {
        val success = tccDepositService.cancel(request.sagaId)
        return TccConfirmResponse(request.sagaId, if (success) "CANCELLED" else "FAILED")
    }
}
