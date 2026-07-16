package {{ cookiecutter.namespace }}.ui.navigation

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.kroegerama.kmp.kaiteki.compose.navigation.ScaffoldSceneDecorator
import {{ cookiecutter.namespace }}.ui.screens.ImageScreen
import {{ cookiecutter.namespace }}.ui.screens.StartScreen
import kotlinx.serialization.Serializable

sealed interface RootNavKey : NavKey {

    @Serializable
    data object Start : RootNavKey

    @Serializable
    data object Details : RootNavKey

}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun rootEntryProvider(
    navigator: Navigator
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<RootNavKey.Start>(
        metadata = ListDetailSceneStrategy.listPane() + ScaffoldSceneDecorator.topAppBar {
            CenterAlignedTopAppBar({ Text("Hello World") })
        }
    ) {
        StartScreen(
            navigator = navigator
        )
    }
    entry<RootNavKey.Details>(
        metadata = ListDetailSceneStrategy.detailPane()
    ) {
        ImageScreen(
            navigator = navigator
        )
    }
}
