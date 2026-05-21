package com.kroegerama.myapp

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.adapter.computeWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import com.kroegerama.myapp.ui.MainActivityContent
import java.time.Instant

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fromSplash()
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
            )
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

        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val timeDone = Instant.now() >= end
                return timeDone.also { done ->
                    if (done) content.viewTreeObserver.removeOnPreDrawListener(this)
                }
            }
        })
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
        val windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(metrics)
        requestedOrientation = when {
            windowSizeClass.isAtLeastBreakpoint(
                widthDpBreakpoint = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
                heightDpBreakpoint = WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND
            ) -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            windowSizeClass.isAtLeastBreakpoint(
                widthDpBreakpoint = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                heightDpBreakpoint = WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
            ) -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
