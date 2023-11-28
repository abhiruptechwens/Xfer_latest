package com.ledgergreen.terminal.di

import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.monitoring.DatadogAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun analytics(impl: DatadogAnalytics): Analytics
}
