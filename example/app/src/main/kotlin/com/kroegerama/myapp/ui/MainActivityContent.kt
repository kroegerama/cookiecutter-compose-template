package com.kroegerama.myapp.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.kroegerama.kmp.kaiteki.compose.rememberChromeCustomTabUriHandler
import com.kroegerama.myapp.ui.dialogs.LoadingDialog
import com.kroegerama.myapp.ui.navigation.Navigator
import com.kroegerama.myapp.ui.navigation.RootNavKey
import com.kroegerama.myapp.ui.navigation.rememberNavigationState
import com.kroegerama.myapp.ui.navigation.rememberScaffoldNavigationStrategy
import com.kroegerama.myapp.ui.navigation.rootEntryProvider
import com.kroegerama.myapp.ui.navigation.toEntries
import com.kroegerama.myapp.ui.scaffold.LocalSharedTransitionScope
import com.kroegerama.myapp.ui.scaffold.LocalSnackbarController
import com.kroegerama.myapp.ui.scaffold.rememberSnackbarController
import com.kroegerama.myapp.ui.theme.AppTheme
import com.kroegerama.myapp.ui.theme.popTransitionSpec
import com.kroegerama.myapp.ui.theme.predictivePopTransitionSpec
import com.kroegerama.myapp.ui.theme.transitionSpec

@Composable
fun MainActivityContent() {
    val viewModel = hiltViewModel<MainActivityViewModel>()

    val chromeCustomTabUriHandler = rememberChromeCustomTabUriHandler()

    val navigationState = rememberNavigationState(
        startRoute = RootNavKey.Start,
        topLevelRoutes = setOf(RootNavKey.Start)
    )
    val navigator = remember { Navigator(navigationState) }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = rememberSnackbarController()
    snackbarController.LaunchSnackbarEffect(snackbarHostState)

    val entryProvider = rootEntryProvider(navigator)
    val navEntries = navigationState.toEntries(entryProvider)
    val loadingState by viewModel.loading.collectAsStateWithLifecycle()

    AppTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalUriHandler provides chromeCustomTabUriHandler,
                LocalSnackbarController provides snackbarController,
                LocalSharedTransitionScope provides this
            ) {
                val scaffoldNavigationStrategy = rememberScaffoldNavigationStrategy<NavKey>(
                    navigator = navigator,
                    sharedTransitionScope = this,
                    bottomAppBarContent = {},
                    snackbarHostState = snackbarHostState
                )
                NavDisplay(
                    entries = navEntries,
                    sceneDecoratorStrategies = listOf(scaffoldNavigationStrategy),
                    transitionSpec = transitionSpec(),
                    popTransitionSpec = popTransitionSpec(),
                    predictivePopTransitionSpec = predictivePopTransitionSpec(),
                    onBack = navigator::goBack,
                )
                loadingState?.let {
                    LoadingDialog(
                        label = it.label
                    )
                }
            }
        }
    }
}
