package com.kroegerama.myapp.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.kroegerama.kmp.kaiteki.compose.navigation.rememberScaffoldSceneDecorator
import com.kroegerama.kmp.kaiteki.compose.rememberChromeCustomTabUriHandler
import com.kroegerama.myapp.ui.dialogs.LoadingDialog
import com.kroegerama.myapp.ui.icons.AppIcons
import com.kroegerama.myapp.ui.icons.Cookie
import com.kroegerama.myapp.ui.navigation.Navigator
import com.kroegerama.myapp.ui.navigation.RootNavKey
import com.kroegerama.myapp.ui.navigation.rememberNavigationState
import com.kroegerama.myapp.ui.navigation.rememberSceneDecorator
import com.kroegerama.myapp.ui.navigation.rootEntryProvider
import com.kroegerama.myapp.ui.navigation.toEntries
import com.kroegerama.myapp.ui.scaffold.LocalSharedTransitionScope
import com.kroegerama.myapp.ui.scaffold.LocalSnackbarController
import com.kroegerama.myapp.ui.scaffold.SnackbarVisuals
import com.kroegerama.myapp.ui.scaffold.rememberSnackbarController
import com.kroegerama.myapp.ui.theme.AppTheme
import com.kroegerama.myapp.ui.theme.popTransitionSpec
import com.kroegerama.myapp.ui.theme.predictivePopTransitionSpec
import com.kroegerama.myapp.ui.theme.transitionSpec

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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

    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = calculatePaneScaffoldDirective(adaptiveInfo)
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = directive,
    )

    AppTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalUriHandler provides chromeCustomTabUriHandler,
                LocalSnackbarController provides snackbarController,
                LocalSharedTransitionScope provides this
            ) {
                val navigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(
                    initialValue = NavigationSuiteScaffoldValue.Hidden
                )
                val scaffoldSceneDecorator = rememberScaffoldSceneDecorator<NavKey>(this)
                val sceneDecorator = rememberSceneDecorator<NavKey>(
                    onShowNavigationSuite = { show ->
                        if (show) {
                            navigationSuiteScaffoldState.show()
                        } else {
                            navigationSuiteScaffoldState.hide()
                        }
                    }
                )

                NavigationSuiteScaffold(
                    navigationItemVerticalArrangement = Arrangement.Center,
                    navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo),
                    state = navigationSuiteScaffoldState,
                    navigationItems = {
                        NavigationItems.entries.forEach { nav ->
                            NavigationSuiteItem(
                                selected = navigationState.topLevelRoute == nav.route,
                                onClick = {
                                    navigator.navigate(nav.route)
                                },
                                icon = { Icon(nav.icon, nav.label()) },
                                label = { Text(nav.label()) }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState
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
                        modifier = Modifier
                            .fillMaxSize()
                            .recalculateWindowInsets()
                    ) { innerPadding ->
                        NavDisplay(
                            entries = navEntries,
                            sceneStrategies = listOf(listDetailSceneStrategy),
                            sceneDecoratorStrategies = listOf(scaffoldSceneDecorator, sceneDecorator),
                            sharedTransitionScope = this,
                            transitionSpec = transitionSpec(),
                            popTransitionSpec = popTransitionSpec(),
                            predictivePopTransitionSpec = predictivePopTransitionSpec(),
                            onBack = navigator::goBack,
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        )
                    }
                }
                loadingState?.let {
                    LoadingDialog(
                        label = it.label
                    )
                }
            }
        }
    }
}

enum class NavigationItems(
    val route: NavKey,
    val icon: ImageVector,
    val label: @Composable () -> String
) {
    Start(
        route = RootNavKey.Start,
        icon = AppIcons.Cookie,
        label = { "Start" }
    )
}
