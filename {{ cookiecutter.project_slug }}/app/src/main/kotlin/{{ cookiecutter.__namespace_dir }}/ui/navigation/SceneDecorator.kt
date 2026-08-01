package {{ cookiecutter.namespace }}.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope

class SceneDecorator<T : Any>(
    private val onShowNavigationSuite: suspend (Boolean) -> Unit
) : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return SceneDecoratorScene(
            scene = scene,
            onShowNavigationSuite = onShowNavigationSuite
        )
    }
}

class SceneDecoratorScene<T : Any>(
    scene: Scene<T>,
    onShowNavigationSuite: suspend (Boolean) -> Unit
) : Scene<T> {
    override val key: Any = scene::class to scene.key
    override val entries: List<NavEntry<T>> = scene.entries
    override val previousEntries: List<NavEntry<T>> = scene.previousEntries
    override val metadata: Map<String, Any> = scene.metadata

    private val lastShowNavigationSuiteEntry = entries.findLast { it.metadata[ShowNavigationSuiteKey] != null }

    override val content: @Composable (() -> Unit) = {
        LaunchedEffect(lastShowNavigationSuiteEntry) {
            val show = lastShowNavigationSuiteEntry?.metadata[ShowNavigationSuiteKey] != false
            onShowNavigationSuite(show)
        }
        scene.content()
    }
}

@Composable
fun <T : Any> rememberSceneDecorator(
    onShowNavigationSuite: suspend (Boolean) -> Unit
): SceneDecorator<T> {
    val currentOnShowNavigationSuite by rememberUpdatedState(onShowNavigationSuite)
    return remember {
        SceneDecorator { show ->
            currentOnShowNavigationSuite(show)
        }
    }
}

/**
 * To hide the NavigationSuite for a screen, use:
 * ```kotlin
 *     entry<ScreenNavKey>(
 *         metadata = metadata {
 *             put(ShowNavigationSuiteKey, false)
 *         }
 *     ) {
 *         Screen()
 *     }
 * ```
 */
data object ShowNavigationSuiteKey : NavMetadataKey<Boolean>
