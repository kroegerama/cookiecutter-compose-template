package {{ cookiecutter.namespace }}.controller

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Singleton
class ProgressController @Inject constructor() {
    private val active = mutableListOf<LoadingState>()
    private val current = MutableStateFlow<LoadingState?>(null)

    // undelayed; blocks input while the dialog is still in its show delay
    val busy: Flow<Boolean> = current.map { it != null }.distinctUntilChanged()

    // anti-flicker: appears only after ShowDelay, then stays visible for at least MinShowTime
    val loading: Flow<LoadingState?> = flow {
        var shownAt: TimeSource.Monotonic.ValueTimeMark? = null
        emitAll(
            current.transformLatest { state ->
                if (state == null) {
                    shownAt?.let { mark ->
                        val remaining = MinShowTime - mark.elapsedNow()
                        if (remaining.isPositive()) delay(remaining)
                    }
                    shownAt = null
                    emit(null)
                } else {
                    if (shownAt == null) {
                        delay(ShowDelay)
                        shownAt = TimeSource.Monotonic.markNow()
                    }
                    emit(state)
                }
            }
        )
    }

    suspend fun <T> loadWithProgress(
        label: String? = null,
        block: suspend () -> T
    ): T {
        val state = LoadingState(
            label = label
        )
        synchronized(active) {
            active += state
            current.value = state
        }
        return try {
            block()
        } finally {
            synchronized(active) {
                active -= state
                current.value = active.lastOrNull()
            }
        }
    }

    @Immutable
    data class LoadingState(
        val label: String? = null
    )

    private companion object {
        val ShowDelay = 150.milliseconds
        val MinShowTime = 500.milliseconds
    }
}
