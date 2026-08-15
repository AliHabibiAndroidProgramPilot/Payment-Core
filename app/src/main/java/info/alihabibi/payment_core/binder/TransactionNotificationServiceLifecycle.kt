package info.alihabibi.payment_core.binder

import android.app.Notification

interface TransactionNotificationServiceLifecycle {
    fun enterForeground(notification: Notification)
    fun updateForeground(notification: Notification)
    fun exitForeground()
    fun stop()
}