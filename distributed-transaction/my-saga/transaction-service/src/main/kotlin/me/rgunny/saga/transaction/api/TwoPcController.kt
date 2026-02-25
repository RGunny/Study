package me.rgunny.saga.transaction.api

import me.rgunny.saga.transaction.service.TwoPcDepositService
import me.rgunny.saga.common.dto.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TwoPcController(
    private val twoPcDepositService: TwoPcDepositService
) {

    @PostMapping("/internal/2pc/prepare")
    fun prepare(@RequestBody request: PrepareRequest): PrepareResponse {
        val success = twoPcDepositService.prepare(request.sagaId, request.accountNumber, request.amount)
        return PrepareResponse(request.sagaId, if (success) "PREPARED" else "ABORT")
    }

    @PostMapping("/internal/2pc/commit")
    fun commit(@RequestBody request: CommitRequest): CommitResponse {
        val success = twoPcDepositService.commitPrepared(request.sagaId)
        return CommitResponse(request.sagaId, if (success) "COMMITTED" else "FAILED")
    }

    @PostMapping("/internal/2pc/rollback")
    fun rollback(@RequestBody request: CommitRequest): CommitResponse {
        val success = twoPcDepositService.rollbackPrepared(request.sagaId)
        return CommitResponse(request.sagaId, if (success) "ABORTED" else "FAILED")
    }

    @PostMapping("/internal/3pc/can-commit")
    fun canCommit(@RequestBody request: CanCommitRequest): CanCommitResponse {
        val vote = twoPcDepositService.canCommit(request.accountNumber, request.amount)
        return CanCommitResponse(vote)
    }
}
