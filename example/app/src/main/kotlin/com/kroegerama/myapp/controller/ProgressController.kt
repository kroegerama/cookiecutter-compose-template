package com.kroegerama.myapp.controller

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressController @Inject constructor() {
    private val _loading = MutableStateFlow<LoadingState?>(null)
    val loading = _loading.asStateFlow()

    suspend fun <T> loadWithProgress(
        label: String? = null,
        block: suspend () -> T
    ): T {
        _loading.update {
            LoadingState(
                label = label
            )
        }
        val minDismiss = System.currentTimeMillis() + 100
        return try {
            block().also {
                val delta = minDismiss - System.currentTimeMillis()
                if (delta > 50) {
                    delay(delta)
                }
            }
        } finally {
            _loading.update { null }
        }
    }

    @Immutable
    data class LoadingState(
        val label: String? = null
    )
}
