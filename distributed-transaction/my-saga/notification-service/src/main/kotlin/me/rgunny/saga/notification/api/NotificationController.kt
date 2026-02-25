package me.rgunny.saga.notification.api

import me.rgunny.saga.notification.service.NotificationService
import me.rgunny.saga.common.dto.NotificationRequest
import me.rgunny.saga.common.dto.NotificationResponse
import org.springframework.web.bind.annotation.*

/**
 * 알림 요청을 수신하는 내부 API 컨트롤러.
 *
 * Orchestration 패턴에서 Transaction Service가 REST 호출로 알림을 요청할 때 사용된다.
 */
@RestController
@RequestMapping("/internal")
class NotificationController(
    private val notificationService: NotificationService
) {
    /** 알림 전송 요청을 처리하고 결과를 반환한다. */
    @PostMapping("/notification")
    fun sendNotification(@RequestBody request: NotificationRequest): NotificationResponse {
        return notificationService.sendNotification(request)
    }
}
