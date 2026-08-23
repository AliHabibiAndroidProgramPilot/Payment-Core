package info.alihabibi.payment_core.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import info.alihabibi.payment_core.data.local.AppDataBase
import info.alihabibi.payment_core.data.local.dao.TransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase =
         Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "app_database"
        ).build()


    @Provides
    @Singleton
    fun provideTransactionDao(dataBase: AppDataBase): TransactionDao = dataBase.transactionDao()


}