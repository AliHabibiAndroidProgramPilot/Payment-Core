package info.alihabibi.aidl_contract.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentRequest(
    val requestId: String,
    val amount: Long,
    val terminalId: String,
    val traceNumber: Long,
    val keepServiceAlive: Boolean = true
) : Parcelable