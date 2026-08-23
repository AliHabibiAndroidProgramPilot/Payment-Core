package info.alihabibi.payment_core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import info.alihabibi.payment_core.data.local.converter.TransactionStatusConverter
import info.alihabibi.payment_core.data.local.dao.TransactionDao
import info.alihabibi.payment_core.data.local.entity.TransactionsEntity

@Database(
    entities = [TransactionsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TransactionStatusConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

}