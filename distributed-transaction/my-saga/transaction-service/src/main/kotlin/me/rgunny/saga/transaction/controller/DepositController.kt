package com.saga.transaction.controller

import com.saga.transaction.service.DepositService
import com.saga.common.dto.DepositRequest
import com.saga.common.dto.DepositResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/internal")
class DepositController(
    private val depositService: DepositService
) {
    @PostMapping("/deposit")
    fun processDeposit(@RequestBody request: DepositRequest): DepositResponse {
        return depositService.processDeposit(request)
    }
}

