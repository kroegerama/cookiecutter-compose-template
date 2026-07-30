package com.kroegerama.myapp.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
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
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.ui.NavDisplay
import com.kroegerama.kmp.kaiteki.compose.navigation.rememberAlertDialogSceneStrategy
import com.kroegerama.kmp.kaiteki.compose.navigation.rememberBottomSheetSceneStrategy
import com.kroegerama.kmp.kaiteki.compose.navigation.rememberScaffoldSceneDecorator
import com.kroegerama.kmp.kaiteki.compose.rememberChromeCustomTabUriHandler
import com.kroegerama.myapp.ui.dialogs.LoadingDialog
import com.kroegerama.myapp.ui.icons.AppIcons
import com.kroegerama.myapp.ui.icons.Cookie
import com.kroegerama.myapp.ui.navigation.LoginNavKey
import com.kroegerama.myapp.ui.navigation.Navigator
import com.kroegerama.myapp.ui.navigation.RootNavKey
import com.kroegerama.myapp.ui.navigation.loginEntryProvider
import com.kroegerama.myapp.ui.navigation.rememberNavigationState
import com.kroegerama.myapp.ui.navigation.rememberSceneDecorator
import com.kroegerama.myapp.ui.navigation.rootEntryProvider
import com.kroegerama.myapp.ui.navigation.toEntries
import com.kroegerama.myapp.ui.scaffold.AppSnackbarHost
import com.kroegerama.myapp.ui.scaffold.LocalSharedTransitionScope
import com.kroegerama.myapp.ui.scaffold.LocalSnackbarController
import com.kroegerama.myapp.ui.scaffold.rememberSnackbarController
import com.kroegerama.myapp.ui.theme.AppTheme

@Composable
fun MainActivityContent() {
    val viewModel = hiltViewModel<MainActivityViewModel>()

    val chromeCustomTabUriHandler = rememberChromeCustomTabUriHandler()

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = rememberSnackbarController()
    snackbarController.LaunchSnackbarEffect(snackbarHostState)

    val loggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()
    val loadingState by viewModel.loading.collectAsStateWithLifecycle()

    val adaptiveInfo = currentWindowAdaptiveInfoV2()

    AppTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalUriHandler provides chromeCustomTabUriHandler,
                LocalSnackbarController provides snackbarController,
                LocalSharedTransitionScope provides this
            ) {
                loggedIn?.let { isLoggedIn ->
                    AnimatedContent(
                        targetState = isLoggedIn,
                        label = "session"
                    ) { state ->
                        if (state) {
                            LoggedInContent(
                                snackbarHostState = snackbarHostState,
                                adaptiveInfo = adaptiveInfo
                            )
                        } else {
                            LoginContent(
                                snackbarHostState = snackbarHostState,
                                adaptiveInfo = adaptiveInfo
                            )
                        }
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

@Composable
private fun SharedTransitionScope.LoginContent(
    snackbarHostState: SnackbarHostState,
    adaptiveInfo: WindowAdaptiveInfo
) {
    val backStack = rememberNavBackStack(LoginNavKey.Login)
    val viewModelStoreProvider = rememberViewModelStoreProvider(key = LoginNavKey.Login)
    val navEntries = rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberResultEventBusNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(viewModelStoreProvider)
        ),
        entryProvider = loginEntryProvider(backStack)
    )

    NavScaffold(
        snackbarHostState = snackbarHostState,
        adaptiveInfo = adaptiveInfo,
        entries = navEntries,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
    )
}

@Composable
private fun SharedTransitionScope.LoggedInContent(
    snackbarHostState: SnackbarHostState,
    adaptiveInfo: WindowAdaptiveInfo
) {
    val navigationState = rememberNavigationState(
        startRoute = RootNavKey.Start,
        topLevelRoutes = setOf(RootNavKey.Start)
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = rootEntryProvider(navigator)
    val navEntries = navigationState.toEntries(entryProvider)

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
                    onClick = dropUnlessResumed {
                        navigator.navigate(nav.route)
                    },
                    icon = { Icon(nav.icon, nav.label()) },
                    label = { Text(nav.label()) }
                )
            }
        }
    ) {
        NavScaffold(
            snackbarHostState = snackbarHostState,
            adaptiveInfo = adaptiveInfo,
            entries = navEntries,
            onBack = navigator::goBack,
            sceneDecoratorStrategies = listOf(scaffoldSceneDecorator, sceneDecorator),
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SharedTransitionScope.NavScaffold(
    snackbarHostState: SnackbarHostState,
    adaptiveInfo: WindowAdaptiveInfo,
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    sceneDecoratorStrategies: List<SceneDecoratorStrategy<NavKey>> = emptyList()
) {
    val directive = calculatePaneScaffoldDirective(adaptiveInfo)
    val bottomSheetSceneStrategy = rememberBottomSheetSceneStrategy<NavKey>()
    val alertDialogSceneStrategy = rememberAlertDialogSceneStrategy<NavKey>()
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = directive,
    )

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets(),
        modifier = Modifier
            .fillMaxSize()
            .recalculateWindowInsets()
    ) { innerPadding ->
        NavDisplay(
            entries = entries,
            sceneStrategies = listOf(
                bottomSheetSceneStrategy,
                alertDialogSceneStrategy,
                listDetailSceneStrategy
            ),
            sceneDecoratorStrategies = sceneDecoratorStrategies,
            sharedTransitionScope = this@NavScaffold,
            onBack = onBack,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
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
