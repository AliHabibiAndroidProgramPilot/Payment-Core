package info.alihabibi.merchant.payment

import android.util.Log
import info.alihabibi.aidl_contract.IPaymentCallback
import info.alihabibi.aidl_contract.PaymentResult

class PaymentCallback : IPaymentCallback.Stub() {

    override fun onTransactionStarted(requestId: String?) {
        Log.i("Ali", "Start Transaction with request id: $requestId")
    }

    override fun onTransactionProgress(requestId: String?, status: String?) {
        Log.i("Ali", "Transaction in progress with request id: $requestId || progress is: $status")
    }

    override fun onTransactionComplete(result: PaymentResult?) {
        Log.i("Ali", "Transaction Success: ${result.toString()}")
    }

    override fun onTransactionFailed(result: PaymentResult?) {
        Log.i("Ali", "Transaction Failed: ${result.toString()}")
    }

}