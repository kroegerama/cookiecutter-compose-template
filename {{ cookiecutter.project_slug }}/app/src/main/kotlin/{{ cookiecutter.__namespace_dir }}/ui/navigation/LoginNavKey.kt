package {{ cookiecutter.namespace }}.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import {{ cookiecutter.namespace }}.ui.screens.LoginScreen
import kotlinx.serialization.Serializable

sealed interface LoginNavKey : NavKey {

    @Serializable
    data object Login : LoginNavKey

}

fun loginEntryProvider(
    backStack: NavBackStack<NavKey>
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<LoginNavKey.Login> {
        LoginScreen(
            backStack = backStack
        )
    }
}
