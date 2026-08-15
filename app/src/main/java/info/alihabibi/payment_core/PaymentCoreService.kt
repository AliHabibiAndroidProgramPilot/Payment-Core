package info.alihabibi.payment_core

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import info.alihabibi.payment_core.binder.NotificationHelper
import info.alihabibi.payment_core.binder.PaymentServiceBinder
import info.alihabibi.payment_core.binder.TransactionNotificationServiceLifecycle
import javax.inject.Inject

@AndroidEntryPoint
class PaymentCoreService : Service() {

    @Inject lateinit var binder: PaymentServiceBinder

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        val notificationLifecycle = object : TransactionNotificationServiceLifecycle {

            override fun enterForeground(notification: Notification) {
                startForeground(NotificationHelper.NOTIFICATION_ID, notification)
            }

            override fun updateForeground(notification: Notification) {
                notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)
            }

            override fun exitForeground() {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }

            override fun stop() {
                stopSelf()
            }

        }
        binder.attach(notificationLifecycle)
    }

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onDestroy() {
        binder.cancelJob()
        super.onDestroy()
    }

}