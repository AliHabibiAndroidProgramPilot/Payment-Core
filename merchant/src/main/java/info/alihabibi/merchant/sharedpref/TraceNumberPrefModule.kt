package info.alihabibi.merchant.sharedpref

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TraceNumberPrefModule {

    @Binds
    abstract fun bindTraceNumberRepository(impl: TraceNumberPrefRepositoryImpl): TraceNumberPrefRepository

}