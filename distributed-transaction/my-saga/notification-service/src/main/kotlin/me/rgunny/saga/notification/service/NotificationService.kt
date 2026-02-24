package me.rgunny.saga.notification.service

import me.rgunny.saga.notification.domain.Notification
import me.rgunny.saga.notification.repository.NotificationRepository
import me.rgunny.saga.common.dto.NotificationRequest
import me.rgunny.saga.common.dto.NotificationResponse
import me.rgunny.saga.common.event.DepositSuccessEvent
import me.rgunny.saga.common.event.WithdrawFailedEvent
import me.rgunny.saga.common.event.NotificationFailedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 알림 전송을 담당하는 서비스.
 *
 * Orchestration 패턴에서는 REST 호출로, Choreography 패턴에서는 Kafka 이벤트 구독으로 알림을 처리한다.
 *
 * 호출 흐름:
 * 1. (Orchestration) Transaction Service → REST → sendNotification
 * 2. (Choreography) DepositSuccessEvent 구독 → handleDepositSuccess
 * 3. (Choreography) WithdrawFailedEvent 구독 → handleWithdrawFailed
 */
@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    /** REST 호출로 알림을 전송한다 (Orchestration 패턴). */
    @Transactional
    fun sendNotification(request: NotificationRequest): NotificationResponse {
        val notification = createNotification(request.userId, request.sagaId, request.notificationType, request.message)
        return NotificationResponse(notification.notificationId, "SENT")
    }

    /** 입금 성공 이벤트를 수신하여 알림을 생성한다. 실패 시 notification.failed 이벤트 발행. */
    @KafkaListener(topics = ["transaction.deposit.success"], groupId = "notification-service-group")
    @Transactional
    fun handleDepositSuccess(event: DepositSuccessEvent) {
        try {
            createNotification(event.accountNumber, event.sagaId, "DEPOSIT_SUCCESS", "Received ${event.amount}")
        } catch (e: Exception) {
            val failedEvent = NotificationFailedEvent(event.sagaId, event.accountNumber, e.message ?: "Notification processing failed")
            kafkaTemplate.send("notification.failed", failedEvent)
            println("[NOTIFICATION] Failed to send deposit success notification: ${e.message}")
        }
    }

    /** 출금 실패 이벤트를 수신하여 알림을 생성한다. */
    @KafkaListener(topics = ["account.withdraw.failed"], groupId = "notification-service-group")
    @Transactional
    fun handleWithdrawFailed(event: WithdrawFailedEvent) {
        createNotification(event.accountNumber, event.sagaId, "WITHDRAW_FAILED", "Withdraw failed: ${event.reason}")
    }

    /** Notification 엔티티를 생성하고 저장한다. */
    private fun createNotification(userId: String, sagaId: String?, type: String, message: String): Notification {
        val notification = Notification(
            notificationId = UUID.randomUUID().toString(),
            userId = userId,
            sagaId = sagaId,
            notificationType = type,
            message = message,
            status = "SENT"
        )
        notificationRepository.save(notification)

        println("[NOTIFICATION] Type: $type, User: $userId, Message: $message")

        return notification
    }
}
