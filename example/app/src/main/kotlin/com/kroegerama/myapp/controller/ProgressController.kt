package com.kroegerama.myapp.controller

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ProgressController @Inject constructor() {
    private val active = mutableListOf<LoadingState>()
    private val _loading = MutableStateFlow<LoadingState?>(null)
    val loading = _loading.asStateFlow()

    suspend fun <T> loadWithProgress(
        label: String? = null,
        block: suspend () -> T
    ): T {
        val state = LoadingState(
            label = label
        )
        synchronized(active) {
            active += state
            _loading.value = state
        }
        val minDismiss = System.currentTimeMillis() + 100
        return try {
            block().also {
                val delta = minDismiss - System.currentTimeMillis()
                if (delta > 50) {
                    delay(delta.milliseconds)
                }
            }
        } finally {
            synchronized(active) {
                active -= state
                _loading.value = active.lastOrNull()
            }
        }
    }

    @Immutable
    data class LoadingState(
        val label: String? = null
    )
}
