package me.rgunny.saga.coordinator.api

import me.rgunny.saga.coordinator.service.OrchestrationTransferService
import me.rgunny.saga.coordinator.service.TwoPcTransferService
import me.rgunny.saga.coordinator.service.ThreePcTransferService
import me.rgunny.saga.coordinator.service.TccTransferService
import me.rgunny.saga.common.dto.TransferRequest
import me.rgunny.saga.common.dto.TransferResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TransferController(
    private val orchestrationTransferService: OrchestrationTransferService,
    private val twoPcTransferService: TwoPcTransferService,
    private val threePcTransferService: ThreePcTransferService,
    private val tccTransferService: TccTransferService
) {
    @PostMapping("/orchestration/transfer")
    fun orchestrationTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return orchestrationTransferService.executeTransfer(request)
    }

    @PostMapping("/2pc/transfer")
    fun twoPcTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return twoPcTransferService.executeTransfer(request)
    }

    @PostMapping("/3pc/transfer")
    fun threePcTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return threePcTransferService.executeTransfer(request)
    }

    @PostMapping("/tcc/transfer")
    fun tccTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return tccTransferService.executeTransfer(request)
    }
}
