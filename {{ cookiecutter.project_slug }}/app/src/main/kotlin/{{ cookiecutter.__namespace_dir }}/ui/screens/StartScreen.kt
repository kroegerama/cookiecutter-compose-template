package {{ cookiecutter.namespace }}.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.kroegerama.kmp.kaiteki.compose.components.ButtonMedium
import com.kroegerama.kmp.kaiteki.compose.components.ButtonSmall
import {{ cookiecutter.namespace }}.api.SessionStore
import {{ cookiecutter.namespace }}.controller.ProgressController
import {{ cookiecutter.namespace }}.ui.navigation.Navigator
import {{ cookiecutter.namespace }}.ui.navigation.RootNavKey
import {{ cookiecutter.namespace }}.ui.scaffold.SnackbarController
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import {{ cookiecutter.namespace }}.ui.theme.dimensions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StartScreen(
    navigator: Navigator
) {
    val viewModel = hiltViewModel<StartScreenViewModel>()

    val actions = StartScreenActions(
        onNavigate = navigator::navigate,
        onProgress = viewModel::performProgress,
        onLogout = viewModel::performLogout
    )

    StartScreenContent(
        actions = actions,
        uiState = viewModel.uiState
    )
}

data class StartScreenUiState(
    val greeting: String
)

private data class StartScreenActions(
    val onNavigate: (NavKey) -> Unit = {},
    val onProgress: () -> Unit = {},
    val onLogout: () -> Unit = {}
)

@Composable
private fun StartScreenContent(
    actions: StartScreenActions,
    uiState: StartScreenUiState
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(MaterialTheme.dimensions.medium)
        ) {
            Text(
                text = uiState.greeting,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(MaterialTheme.dimensions.small)
            )
            ButtonMedium(
                onClick = actions.onProgress,
                text = "Progress"
            )
            ButtonSmall(
                onClick = { actions.onNavigate(RootNavKey.Details) },
                text = "Details"
            )
            ButtonSmall(
                onClick = actions.onLogout,
                text = "Logout"
            )
        }
    }
}

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val progressController: ProgressController,
    private val snackbarController: SnackbarController,
    private val sessionStore: SessionStore
) : ViewModel() {

    var uiState by mutableStateOf(StartScreenUiState("Hello App"))

    fun performProgress() {
        viewModelScope.launch {
            progressController.loadWithProgress {
                delay(2000.milliseconds)
                snackbarController.showSuccess("Success!!!")
            }
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            sessionStore.clearBearer()
        }
    }
}

@Preview
@Composable
private fun StartScreenPreview() {
    AppTheme {
        StartScreenContent(
            actions = StartScreenActions(),
            uiState = StartScreenUiState("Hello World")
        )
    }
}
