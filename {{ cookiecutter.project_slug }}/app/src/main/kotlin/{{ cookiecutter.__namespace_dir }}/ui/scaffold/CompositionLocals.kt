package {{ cookiecutter.namespace }}.ui.scaffold

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope> = compositionLocalOf {
    error("No LocalSharedTransitionScope provided")
}
