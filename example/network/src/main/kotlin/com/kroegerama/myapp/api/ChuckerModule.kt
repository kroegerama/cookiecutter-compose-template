package com.kroegerama.myapp.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChuckerModule {
    @Provides
    @Singleton
    fun provideChuckerCollector(
        @ApplicationContext context: Context
    ): ChuckerCollector = ChuckerCollector(context, showNotification = false)

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context,
        collector: ChuckerCollector,
    ): ChuckerInterceptor = ChuckerInterceptor.Builder(context)
        .collector(collector)
        .alwaysReadResponseBody(true)
        .build()
}
