package me.rgunny.saga.notification.repository

import me.rgunny.saga.notification.domain.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, String>
