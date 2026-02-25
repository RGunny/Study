package me.rgunny.saga.account.api

import me.rgunny.saga.account.service.ChoreographyAccountService
import me.rgunny.saga.common.dto.TransferRequest
import me.rgunny.saga.common.dto.TransferResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TransferController(
    private val choreographyAccountService: ChoreographyAccountService
) {

    @PostMapping("/choreography/transfer")
    fun choreographyTransfer(@RequestBody request: TransferRequest): TransferResponse {
        return choreographyAccountService.initiateTransfer(request)
    }
}
