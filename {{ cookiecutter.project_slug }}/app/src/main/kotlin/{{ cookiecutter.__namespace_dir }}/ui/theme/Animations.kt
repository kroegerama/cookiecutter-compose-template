package {{ cookiecutter.namespace }}.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

/** Shared-axis slide distance (Material token value ). */
private val SlideDistance = 30.dp

@Immutable
class NavigationTransitionSpecs<T : Any>(
    val push: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform,
    val pop: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform,
    val predictivePop: AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform,
)

@Composable
fun <T : Any> rememberNavigationTransitionSpecs(): NavigationTransitionSpecs<T> {
    val motion = MaterialTheme.motionScheme
    val slidePx = with(LocalDensity.current) { SlideDistance.roundToPx() }

    return remember(motion, slidePx) {
        val spatial = motion.defaultSpatialSpec<IntOffset>()
        val spatialFloat = motion.defaultSpatialSpec<Float>()
        val effects = motion.defaultEffectsSpec<Float>()

        NavigationTransitionSpecs(
            push = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally(spatial) { slidePx } + fadeIn(effects),
                    initialContentExit = slideOutHorizontally(spatial) { -slidePx } + fadeOut(effects)
                )
            },
            pop = {
                ContentTransform(
                    targetContentEnter = slideInHorizontally(spatial) { -slidePx } + fadeIn(effects),
                    initialContentExit = slideOutHorizontally(spatial) { slidePx } + fadeOut(effects)
                )
            },
            predictivePop = { edge ->
                val enter = slideInHorizontally(spatial) { -slidePx } + fadeIn(effects)
                val exit = when (edge) {
                    NavigationEvent.EDGE_LEFT -> scaleOut(
                        animationSpec = spatialFloat,
                        targetScale = 0.9f,
                        transformOrigin = TransformOrigin(1f, 0.5f),
                    ) + fadeOut(effects)

                    NavigationEvent.EDGE_RIGHT -> scaleOut(
                        animationSpec = spatialFloat,
                        targetScale = 0.9f,
                        transformOrigin = TransformOrigin(0.5f, 0.5f),
                    ) + fadeOut(effects)

                    // 3-button back: same as regular pop exit
                    else -> slideOutHorizontally(spatial) { slidePx } + fadeOut(effects)
                }
                ContentTransform(
                    targetContentEnter = enter,
                    initialContentExit = exit
                )
            }
        )
    }
}
