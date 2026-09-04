package com.kroegerama.myapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.kroegerama.myapp.api.ApiSetup
import com.kroegerama.myapp.controller.LogoutHandler
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var apiSetup: ApiSetup

    @Inject
    lateinit var logoutHandler: LogoutHandler

    @Inject
    lateinit var imageLoader: Lazy<ImageLoader>

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(
            this,
            minPriority = LogPriority.VERBOSE
        )
        apiSetup.install()
        logoutHandler.start()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}
