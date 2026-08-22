package info.alihabibi.payment_core.data.local.converter

import androidx.room.TypeConverter
import info.alihabibi.payment_core.domain.model.TransactionStatus
import java.lang.IllegalArgumentException

class TransactionStatusConverter {

    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String = status.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus? {
        return try {
            TransactionStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

}