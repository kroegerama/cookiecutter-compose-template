package {{ cookiecutter.namespace }}.ui.navigation

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import {{ cookiecutter.namespace }}.ui.scaffold.SnackbarVisuals
import {{ cookiecutter.namespace }}.ui.scaffold.cacheSize

data class ScaffoldNavigationScene<T : Any>(
    private val navigator: Navigator,
    private val scene: Scene<T>,
    private val sharedTransitionScope: SharedTransitionScope,
    private val bottomAppBarContent: @Composable () -> Unit,
    private val snackbarHostState: SnackbarHostState
) : Scene<T> {
    override val key = scene::class to scene.key

    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val metadata = scene.metadata

    @OptIn(ExperimentalAnimationApi::class)
    override val content = @Composable {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        val isMovableContentCaller = animatedContentScope.transition.targetState == EnterExitState.Visible

        with(sharedTransitionScope) {
            Scaffold(
                topBar = topBar@{
                    Box(
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("top.bar"),
                                animatedContentScope
                            )
                    ) {
                        if (isMovableContentCaller) {
                            scene.metadata[TopAppBarKey]?.let { topAppBarContent ->
                                topAppBarContent()
                            }
                        }
                    }
                },
                bottomBar = bottomBar@{
                    Box(
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("bottom.bar"),
                                animatedContentScope
                            )
                    ) {
                        if (isMovableContentCaller) {
                            bottomAppBarContent()
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("bottom.bar"),
                                animatedContentScope
                            )
                    ) { data ->
                        when (val visuals = data.visuals) {
                            is SnackbarVisuals -> Snackbar(
                                snackbarData = data,
                                containerColor = visuals.containerColor,
                                contentColor = visuals.contentColor,
                                modifier = Modifier.safeDrawingPadding()
                            )

                            else -> Snackbar(
                                snackbarData = data,
                                modifier = Modifier.safeDrawingPadding()
                            )
                        }
                    }
                },
                contentWindowInsets = WindowInsets(),
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    scene.content()
                }
            }
        }
    }
}

@Composable
fun <T : Any> rememberScaffoldNavigationStrategy(
    navigator: Navigator,
    sharedTransitionScope: SharedTransitionScope,
    bottomAppBarContent: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState
): ScaffoldNavigationStrategy<T> {
    val currentBottomAppBarContent by rememberUpdatedState(bottomAppBarContent)

    return remember(sharedTransitionScope) {
        ScaffoldNavigationStrategy(
            navigator = navigator,
            sharedTransitionScope = sharedTransitionScope,
            bottomAppBarContent = currentBottomAppBarContent,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * To show the TopAppBar for a screen, use:
 * ```kotlin
 *     entry<ScreenNavKey>(
 *         metadata = metadata {
 *             put(TopAppBarKey) {
 *                 TopAppBar(
 *                     title = { Text("Hello World") }
 *                 )
 *             }
 *         }
 *     ) {
 *         Screen()
 *     }
 * ```
 */
data object TopAppBarKey : NavMetadataKey<@Composable () -> Unit>

/**
 * To show the BottomNav for a screen, use:
 * ```kotlin
 *     entry<ScreenNavKey>(
 *         metadata = metadata {
 *             put(BottomBarKey, true)
 *         }
 *     ) {
 *         Screen()
 *     }
 * ```
 */
data object BottomBarKey : NavMetadataKey<Boolean>

class ScaffoldNavigationStrategy<T : Any>(
    private val navigator: Navigator,
    private val sharedTransitionScope: SharedTransitionScope,
    private val bottomAppBarContent: @Composable () -> Unit,
    private val snackbarHostState: SnackbarHostState
) : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return ScaffoldNavigationScene(
            navigator = navigator,
            scene = scene,
            sharedTransitionScope = sharedTransitionScope,
            bottomAppBarContent = bottomAppBarContent,
            snackbarHostState = snackbarHostState
        )
    }
}
