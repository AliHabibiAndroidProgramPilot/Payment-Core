package info.alihabibi.aidl_contract.model

import android.os.Parcelable
import info.alihabibi.aidl_contract.ResultStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentResult(
    val requestId: String,
    val status: String,
    val responseCode: String?,
    val rrn: String?,
    val message: String?,
    val durationMs: Long
) : Parcelable
