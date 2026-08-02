package {{ cookiecutter.namespace }}.ui.scaffold

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kroegerama.kmp.kaiteki.compose.scaffold.SnackbarColors
import com.kroegerama.kmp.kaiteki.compose.scaffold.SnackbarController
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class AppSnackbarController @Inject constructor(
    @ApplicationContext private val context: Context
) : SnackbarController() {
    fun show(
        @StringRes messageRes: Int,
        @StringRes actionLabelRes: Int? = null,
        onAction: (() -> Unit)? = null,
        colors: SnackbarColors = SnackbarColors.Default
    ) = show(
        message = context.getString(messageRes),
        actionLabel = actionLabelRes?.let(context::getString),
        onAction = onAction,
        colors = colors
    )

    fun showError(
        @StringRes messageRes: Int,
        @StringRes actionLabelRes: Int? = null,
        onAction: (() -> Unit)? = null
    ) = showError(
        message = context.getString(messageRes),
        actionLabel = actionLabelRes?.let(context::getString),
        onAction = onAction
    )
}

@Composable
fun rememberSnackbarController(): AppSnackbarController {
    val context = LocalContext.current
    val snackbarController = remember(context) {
        EntryPointAccessors.fromApplication<SnackbarControllerEntryPoint>(context).snackbarController()
    }
    return snackbarController
}

val LocalSnackbarController: ProvidableCompositionLocal<AppSnackbarController> = compositionLocalOf {
    error("No SnackbarController provided.")
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SnackbarControllerEntryPoint {
    fun snackbarController(): AppSnackbarController
}
