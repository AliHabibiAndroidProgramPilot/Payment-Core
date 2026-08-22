package info.alihabibi.payment_core.binder

import android.os.RemoteException
import android.util.Log
import info.alihabibi.aidl_contract.IPaymentCallback
import info.alihabibi.aidl_contract.IPaymentService
import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import info.alihabibi.payment_core.domain.TransactionStateEvent
import info.alihabibi.payment_core.domain.model.local.TransactionStatus
import info.alihabibi.payment_core.domain.usecase.GetTransactionUseCase
import info.alihabibi.payment_core.domain.usecase.StartTransactionUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class PaymentServiceBinder @Inject constructor(
    private val startTransactionUseCase: StartTransactionUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val notificationHelper: NotificationHelper
) : IPaymentService.Stub() {

    private val binderJob = SupervisorJob()
    private val binderScope = CoroutineScope(Dispatchers.IO + binderJob)
    private val isTransactionInProcess = AtomicBoolean(false)

    internal fun cancelJob() = binderJob.cancel()

    private lateinit var notificationLifecycle: TransactionNotificationServiceLifecycle
    fun attach(notificationLifecycle: TransactionNotificationServiceLifecycle) {
        this.notificationLifecycle = notificationLifecycle
    }

    override fun startTransaction(request: PaymentRequest, callback: IPaymentCallback) {
        if (!isTransactionInProcess.compareAndSet(false, true)) {
            try {
                callback.onTransactionFailed(
                    PaymentResult(
                        requestId = request.requestId,
                        status = TransactionStatus.FAILED.name,
                        responseCode = null,
                        rrn = null,
                        message = "Another Transaction Is In Process!",
                        durationMs = 0L
                    )
                )
            } catch (e: RemoteException) {
                Log.i("Bind", "AIDL Bind Exception Occurred")
                e.printStackTrace()
            }
            return
        }

        binderScope.launch {
            try {
                notificationLifecycle.enterForeground(notificationHelper.processTransactionNotification())
                callback.onTransactionStarted(request.requestId)
                startTransactionUseCase.invoke(request).collect { event ->
                    when (event) {
                        is TransactionStateEvent.StateChanged -> {
                            when (event.state) {
                                TransactionStatus.CONNECTING -> {
                                    notificationLifecycle.updateForeground(
                                        notificationHelper.connectingTransactionNotification()
                                    )
                                }
                                TransactionStatus.WAITING_RESPONSE -> {
                                    notificationLifecycle.updateForeground(
                                        notificationHelper.waitingToResponseTransactionNotification()
                                    )
                                }
                                else -> Unit
                            }
                            callback.onTransactionProgress(request.requestId, event.state.name)
                        }

                        is TransactionStateEvent.Success -> {
                            notificationLifecycle.updateForeground(
                                notificationHelper.successTransactionNotification()
                            )
                            callback.onTransactionComplete(event.result)
                        }

                        is TransactionStateEvent.Failure -> {
                            notificationLifecycle.updateForeground(
                                notificationHelper.failedTransactionNotification(event.result.message)
                            )
                            callback.onTransactionFailed(event.result)
                        }
                    }
                }
            } catch (_: RemoteException) {
            } finally {
                isTransactionInProcess.set(false)
                if (!request.keepServiceAlive) {
                    notificationLifecycle.exitForeground()
                    notificationLifecycle.stop()
                }
            }
        }
    }

    override fun getTransactionStatus(requestId: String): PaymentResult {
        return runBlocking { getTransactionUseCase.invoke(requestId) }
    }

}