package {{ cookiecutter.namespace }}.ui.scaffold

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class SnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val semantics: SnackbarSemantics = SnackbarSemantics.Default
) : androidx.compose.material3.SnackbarVisuals {
    enum class SnackbarSemantics(
        val containerColor: @Composable () -> Color = { SnackbarDefaults.color },
        val contentColor: @Composable () -> Color = { SnackbarDefaults.contentColor },
        val actionColor: @Composable () -> Color = { SnackbarDefaults.actionColor },
        val actionContentColor: @Composable () -> Color = { SnackbarDefaults.actionContentColor },
    ) {
        Default,
        Success(
//            containerColor = { MaterialTheme.colorScheme.success },
//            contentColor = { MaterialTheme.colorScheme.onSuccess },
//            actionColor = { },
//            actionContentColor = { },
        ),
        Error(
            containerColor = { MaterialTheme.colorScheme.errorContainer },
            contentColor = { MaterialTheme.colorScheme.onErrorContainer },
            actionColor = { MaterialTheme.colorScheme.error },
            actionContentColor = { MaterialTheme.colorScheme.onError },
        )
    }
}

@Singleton
class SnackbarController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val channel: Channel<SnackbarVisuals> = Channel(Channel.CONFLATED)

    fun showSuccess(@StringRes messageRes: Int) =
        showSuccess(context.getString(messageRes))

    fun showSuccess(message: String) {
        channel.trySend(
            SnackbarVisuals(
                message = message,
                semantics = SnackbarVisuals.SnackbarSemantics.Success
            )
        )
    }

    @Composable
    fun LaunchSnackbarEffect(snackbarHostState: SnackbarHostState) {
        LaunchedEffect(this, snackbarHostState) {
            channel.receiveAsFlow().collectLatest { visuals ->
                snackbarHostState.showSnackbar(visuals)
            }
        }
    }
}

@Composable
fun AppSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState
    ) { data ->
        when (val visuals = data.visuals) {
            is SnackbarVisuals -> Snackbar(
                snackbarData = data,
                containerColor = visuals.semantics.containerColor(),
                contentColor = visuals.semantics.contentColor(),
                actionColor = visuals.semantics.actionColor(),
                actionContentColor = visuals.semantics.actionContentColor(),
                modifier = Modifier.safeDrawingPadding()
            )

            else -> Snackbar(
                snackbarData = data,
                modifier = Modifier.safeDrawingPadding()
            )
        }
    }
}

@Composable
fun rememberSnackbarController(): SnackbarController {
    val context = LocalContext.current
    val snackbarController = remember(context) {
        EntryPointAccessors.fromApplication<SnackbarControllerEntryPoint>(context).snackbarController()
    }
    return snackbarController
}

val LocalSnackbarController: ProvidableCompositionLocal<SnackbarController> = compositionLocalOf {
    error("No SnackbarController provided.")
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SnackbarControllerEntryPoint {
    fun snackbarController(): SnackbarController
}
