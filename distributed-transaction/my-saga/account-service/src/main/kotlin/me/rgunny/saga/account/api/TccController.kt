package me.rgunny.saga.account.api

import me.rgunny.saga.account.service.TccAccountService
import me.rgunny.saga.common.dto.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TccController(
    private val tccAccountService: TccAccountService
) {

    @PostMapping("/internal/tcc/try")
    fun tryReserve(@RequestBody request: TccTryRequest): TccTryResponse {
        val success = tccAccountService.tryReserve(request.sagaId, request.accountNumber, request.amount)
        return TccTryResponse(request.sagaId, if (success) "RESERVED" else "FAILED")
    }

    @PostMapping("/internal/tcc/confirm")
    fun confirm(@RequestBody request: TccConfirmRequest): TccConfirmResponse {
        val success = tccAccountService.confirm(request.sagaId)
        return TccConfirmResponse(request.sagaId, if (success) "CONFIRMED" else "FAILED")
    }

    @PostMapping("/internal/tcc/cancel")
    fun cancel(@RequestBody request: TccConfirmRequest): TccConfirmResponse {
        val success = tccAccountService.cancel(request.sagaId)
        return TccConfirmResponse(request.sagaId, if (success) "CANCELLED" else "FAILED")
    }
}
