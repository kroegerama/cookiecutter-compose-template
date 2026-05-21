package {{ cookiecutter.namespace }}.ui.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey

@Stable
class Navigator(
    val state: NavigationState
) {
    fun navigate(route: NavKey, singleTop: Boolean = false) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else {
            if (singleTop) {
                state.backStacks[state.topLevelRoute]?.removeAll { it::class == route::class }
            }
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
