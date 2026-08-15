package info.alihabibi.payment_core.binder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import info.alihabibi.payment_core.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "payment_processing_channel"
        const val NOTIFICATION_ID = 18
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Payment processing",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
            description = "Shows the live status of an in-progress payment transaction"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        title: String,
        contentText: String,
        isOnGoing: Boolean
    ): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_transaction)
            .setOngoing(isOnGoing)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

    fun processTransactionNotification(): Notification =
        buildNotification(
            "Transaction is in process",
            "In Progress",
            true
        )

    fun connectingTransactionNotification(): Notification =
        buildNotification(
            "Connecting",
            "Connecting to server...",
            true
        )

    fun waitingToResponseTransactionNotification(): Notification =
        buildNotification(
            "Waiting",
            "Waiting to respons...",
            true
        )

    fun successTransactionNotification(): Notification =
        buildNotification(
            "Complete",
            "Complete successfully transaction",
            false
        )

    fun failedTransactionNotification(message: String?): Notification = buildNotification(
        "Failed",
        "Failure or timeout | $message",
        false
    )

}