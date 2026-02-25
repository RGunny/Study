package me.rgunny.saga.transaction.api

import me.rgunny.saga.transaction.service.OrchestrationDepositService
import me.rgunny.saga.common.dto.DepositRequest
import me.rgunny.saga.common.dto.DepositResponse
import org.springframework.web.bind.annotation.*

/**
 * 입금 요청을 수신하는 내부 API 컨트롤러.
 *
 * Orchestration 패턴에서 saga-coordinator가 REST 호출로 입금을 요청할 때 사용된다.
 */
@RestController
@RequestMapping("/internal")
class DepositController(
    private val orchestrationDepositService: OrchestrationDepositService
) {
    /** 입금 요청을 처리하고 결과를 반환한다. */
    @PostMapping("/deposit")
    fun processDeposit(@RequestBody request: DepositRequest): DepositResponse {
        return orchestrationDepositService.processDeposit(request)
    }
}
