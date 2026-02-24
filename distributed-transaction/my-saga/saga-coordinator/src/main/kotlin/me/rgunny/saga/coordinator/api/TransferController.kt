package me.rgunny.saga.coordinator.api

import me.rgunny.saga.coordinator.service.OrchestrationService
import me.rgunny.saga.common.dto.TransferRequest
import me.rgunny.saga.common.dto.TransferResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TransferController(
    private val orchestrationService: OrchestrationService
) {
    @PostMapping("/orchestration/transfer")
    fun orchestrationTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return orchestrationService.executeTransfer(request)
    }
}
