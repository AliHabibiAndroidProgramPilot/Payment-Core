package info.alihabibi.merchant.payment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import info.alihabibi.aidl_contract.IPaymentCallback
import info.alihabibi.aidl_contract.IPaymentService
import info.alihabibi.aidl_contract.PaymentRequest
import info.alihabibi.aidl_contract.PaymentResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentCoreConnector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var service: IPaymentService? = null
    private var bindDeferred: CompletableDeferred<IPaymentService> = CompletableDeferred()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val paymentService = IPaymentService.Stub.asInterface(binder)
            service = paymentService
            if (!bindDeferred.isCompleted) bindDeferred.complete(paymentService)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            bindDeferred = CompletableDeferred()
        }

    }

    fun bind() {
        val intent = Intent("info.alihabibi.payment_core.ACTION_BIND_PAYMENT").apply {
            setPackage("info.alihabibi.payment_core")
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        runCatching { context.unbindService(connection) }
        service = null
    }

    private suspend fun awaitPaymentService(): IPaymentService = service ?: bindDeferred.await()

    fun startTransaction(request: PaymentRequest): Flow<PaymentEvent> = callbackFlow {
        val paymentService = try {
            awaitPaymentService()
        } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }

        val callback = object : IPaymentCallback.Stub() {

            override fun onTransactionStarted(requestId: String) {
                trySend(PaymentEvent.Started(requestId))
                Log.i("Ali", "Start Transaction with request id: $requestId")
            }

            override fun onTransactionProgress(requestId: String, status: String) {
                trySend(PaymentEvent.Progress(requestId, status))
                Log.i("Ali", "Transaction in progress with request id: $requestId || progress is: $status")
            }

            override fun onTransactionComplete(result: PaymentResult) {
                trySend(PaymentEvent.Completed(result))
                Log.i("Ali", "Transaction Success: ${result.toString()}")
            }

            override fun onTransactionFailed(result: PaymentResult) {
                trySend(PaymentEvent.Failed(result))
                Log.i("Ali", "Transaction Failed: ${result.toString()}")
            }

        }

        try {
            paymentService.startTransaction(request, callback)
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {  }
    }

    suspend fun getTransactionStatus(requestId: String): PaymentResult? = withContext(Dispatchers.IO) {
        awaitPaymentService().getTransactionStatus(requestId)
    }

}