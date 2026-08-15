package info.alihabibi.payment_core.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import info.alihabibi.payment_core.domain.usecase.GetTransactionUseCase
import info.alihabibi.payment_core.domain.usecase.StartTransactionUseCase

@Module
@InstallIn(ServiceComponent::class)
object UseCasesModule {

    @Provides
    @ServiceScoped
    fun provideStartTransactionUseCase(
        transactionRepository: TransactionRepository
    ): StartTransactionUseCase = StartTransactionUseCase(transactionRepository)

    @Provides
    @ServiceScoped
    fun provideGetTransactionUseCase(
        transactionRepository: TransactionRepository
    ): GetTransactionUseCase = GetTransactionUseCase(transactionRepository)

}