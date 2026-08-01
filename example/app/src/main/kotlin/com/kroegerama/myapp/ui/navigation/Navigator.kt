package com.kroegerama.myapp.ui.navigation

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
            return
        }
        val stack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        if (singleTop && stack.lastOrNull()?.let { it::class } == route::class) {
            // already on top: replace, so the new arguments take effect
            stack[stack.lastIndex] = route
        } else {
            stack.add(route)
        }
    }

    /** Pops the given top level route's stack back to its base entry. */
    fun resetStack(topLevelRoute: NavKey = state.topLevelRoute) {
        val stack = state.backStacks[topLevelRoute] ?: return
        while (stack.size > 1) stack.removeAt(stack.lastIndex)
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.lastOrNull() ?: return

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
