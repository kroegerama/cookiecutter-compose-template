package {{ cookiecutter.namespace }}.ui

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
import {{ cookiecutter.namespace }}.ui.dialogs.LoadingDialog
import {{ cookiecutter.namespace }}.ui.navigation.Navigator
import {{ cookiecutter.namespace }}.ui.navigation.RootNavKey
import {{ cookiecutter.namespace }}.ui.navigation.rememberNavigationState
import {{ cookiecutter.namespace }}.ui.navigation.rememberScaffoldNavigationStrategy
import {{ cookiecutter.namespace }}.ui.navigation.rootEntryProvider
import {{ cookiecutter.namespace }}.ui.navigation.toEntries
import {{ cookiecutter.namespace }}.ui.scaffold.LocalSharedTransitionScope
import {{ cookiecutter.namespace }}.ui.scaffold.LocalSnackbarController
import {{ cookiecutter.namespace }}.ui.scaffold.rememberSnackbarController
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import {{ cookiecutter.namespace }}.ui.theme.popTransitionSpec
import {{ cookiecutter.namespace }}.ui.theme.predictivePopTransitionSpec
import {{ cookiecutter.namespace }}.ui.theme.transitionSpec

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
