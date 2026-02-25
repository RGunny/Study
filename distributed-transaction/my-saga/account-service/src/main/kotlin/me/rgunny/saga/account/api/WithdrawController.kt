package me.rgunny.saga.account.api

import me.rgunny.saga.account.service.OrchestrationAccountService
import me.rgunny.saga.common.dto.WithdrawRequest
import me.rgunny.saga.common.dto.WithdrawResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 출금 요청을 수신하는 내부 API 컨트롤러.
 *
 * Orchestration 패턴에서 saga-coordinator가 REST 호출로 출금/보상을 요청할 때 사용된다.
 */
@RestController
@RequestMapping("/internal")
class WithdrawController(
    private val orchestrationAccountService: OrchestrationAccountService
) {
    /** 출금 요청을 처리하고 결과를 반환한다. */
    @PostMapping("/withdraw")
    fun withdraw(@RequestBody request: WithdrawRequest): WithdrawResponse {
        val result = orchestrationAccountService.withdraw(request.sagaId, request.accountNumber, request.amount)
            ?: return WithdrawResponse(transactionId = "", status = "FAILED")
        return WithdrawResponse(transactionId = result.transactionId, status = "COMPLETED")
    }

    /** 출금 보상 요청을 처리한다 (잔액 복원). */
    @PostMapping("/withdraw/compensate")
    fun compensateWithdraw(@RequestBody request: WithdrawRequest): WithdrawResponse {
        orchestrationAccountService.compensateWithdraw(request.accountNumber, request.amount)
        return WithdrawResponse(transactionId = "", status = "COMPENSATED")
    }
}
