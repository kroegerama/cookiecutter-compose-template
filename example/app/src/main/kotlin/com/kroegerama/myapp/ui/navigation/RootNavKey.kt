package com.kroegerama.myapp.ui.navigation

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import com.kroegerama.myapp.ui.screens.StartScreen
import kotlinx.serialization.Serializable

sealed interface RootNavKey : NavKey {

    @Serializable
    data object Start : RootNavKey

    @Serializable
    data object Details : RootNavKey

}

fun rootEntryProvider(
    navigator: Navigator
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<RootNavKey.Start>(
        metadata = metadata {
            put(TopAppBarKey) {
                TopAppBar(
                    title = { Text("Hello World") }
                )
            }
            put(BottomBarKey, true)
        }
    ) {
        StartScreen(
            navigator = navigator
        )
    }
    entry<RootNavKey.Details> {
        Text("Details", Modifier.safeDrawingPadding())
    }
}
