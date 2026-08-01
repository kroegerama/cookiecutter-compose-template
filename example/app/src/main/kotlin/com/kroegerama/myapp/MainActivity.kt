package com.kroegerama.myapp

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.adapter.computeWindowSizeClass
import com.kroegerama.myapp.ui.MainActivityContent
import com.kroegerama.myapp.ui.MainActivityViewModel
import com.kroegerama.myapp.ui.theme.NavigationBarStyle
import com.kroegerama.myapp.ui.theme.StatusBarStyle
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fromSplash()
        }
        enableEdgeToEdge(
            statusBarStyle = StatusBarStyle,
            navigationBarStyle = NavigationBarStyle
        )
        updateOrientation()
        setContent {
            MainActivityContent()
        }
        addConfigurationChangedViewHook()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun fromSplash() {
        val end: Instant = Instant.now().plusMillis(400)
        val content: View = findViewById(android.R.id.content)

        val listener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val timeDone = Instant.now() >= end
                val sessionLoaded = viewModel.loggedIn.value != null
                return (timeDone && sessionLoaded).also { done ->
                    if (done) content.viewTreeObserver.removeOnPreDrawListener(this)
                }
            }
        }
        content.viewTreeObserver.addOnPreDrawListener(listener)
        // upper bound, so a stalled session read cannot hold the splash forever
        lifecycleScope.launch {
            delay(5.seconds)
            content.viewTreeObserver.removeOnPreDrawListener(listener)
            content.invalidate()
        }
    }

    private fun addConfigurationChangedViewHook() {
        // see https://developer.android.com/develop/ui/compose/quick-guides/content/restrict-app-orientation-on-phones#override_app_manifest
        val container = findViewById<ViewGroup>(android.R.id.content)

        container.addView(object : View(this) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                updateOrientation()
            }
        })
    }

    private fun updateOrientation() {
        val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(this)
        val windowSizeClass = WindowSizeClass.BREAKPOINTS_V2.computeWindowSizeClass(metrics)
        // opinionated default: compact (phone-sized) windows are locked to portrait
        requestedOrientation = when {
            windowSizeClass.isAtLeastBreakpoint(
                widthDpBreakpoint = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                heightDpBreakpoint = WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
            ) -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
