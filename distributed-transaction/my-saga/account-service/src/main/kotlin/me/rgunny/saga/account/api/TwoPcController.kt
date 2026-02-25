package me.rgunny.saga.account.api

import me.rgunny.saga.account.service.TwoPcAccountService
import me.rgunny.saga.common.dto.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TwoPcController(
    private val twoPcAccountService: TwoPcAccountService
) {

    @PostMapping("/internal/2pc/prepare")
    fun prepare(@RequestBody request: PrepareRequest): PrepareResponse {
        val success = twoPcAccountService.prepare(request.sagaId, request.accountNumber, request.amount)
        return PrepareResponse(request.sagaId, if (success) "PREPARED" else "ABORT")
    }

    @PostMapping("/internal/2pc/commit")
    fun commit(@RequestBody request: CommitRequest): CommitResponse {
        val success = twoPcAccountService.commitPrepared(request.sagaId)
        return CommitResponse(request.sagaId, if (success) "COMMITTED" else "FAILED")
    }

    @PostMapping("/internal/2pc/rollback")
    fun rollback(@RequestBody request: CommitRequest): CommitResponse {
        val success = twoPcAccountService.rollbackPrepared(request.sagaId)
        return CommitResponse(request.sagaId, if (success) "ABORTED" else "FAILED")
    }

    @PostMapping("/internal/3pc/can-commit")
    fun canCommit(@RequestBody request: CanCommitRequest): CanCommitResponse {
        val vote = twoPcAccountService.canCommit(request.accountNumber, request.amount)
        return CanCommitResponse(vote)
    }
}
