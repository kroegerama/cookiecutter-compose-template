package {{ cookiecutter.namespace }}.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

private const val TRANSITION_DURATION = 400

fun <T : Any> transitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(TRANSITION_DURATION)),
        fadeOut(animationSpec = tween(TRANSITION_DURATION)),
    )
}

fun <T : Any> popTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(TRANSITION_DURATION)),
        fadeOut(animationSpec = tween(TRANSITION_DURATION)),
    )
}

fun <T : Any> predictivePopTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(TRANSITION_DURATION)),
        fadeOut(animationSpec = tween(TRANSITION_DURATION)),
    )
}
