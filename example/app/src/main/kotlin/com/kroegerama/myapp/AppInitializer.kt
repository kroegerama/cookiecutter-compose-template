package com.kroegerama.myapp

import android.content.Context
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.useExistingImageAsPlaceholder
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.kroegerama.myapp.api.Api
import com.kroegerama.myapp.api.ApiInitializer
import com.kroegerama.myapp.controller.LogoutHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class AppInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppInitializerEntryPoint {
        fun getLogoutHandler(): LogoutHandler
    }

    private lateinit var logoutHandler: LogoutHandler

    @OptIn(ExperimentalCoilApi::class)
    override fun create(context: Context) {
        val accessor = EntryPointAccessors.fromApplication<AppInitializerEntryPoint>(context)
        logoutHandler = accessor.getLogoutHandler()

        logoutHandler.init()

        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(700)
                .useExistingImageAsPlaceholder(true)
                .components {
                    add(KtorNetworkFetcherFactory(httpClient = { Api.client }))
                }
                .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
                .build()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        ApiInitializer::class.java
    )
}
