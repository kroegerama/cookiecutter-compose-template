package {{ cookiecutter.namespace }}.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kroegerama.kmp.kaiteki.compose.components.ButtonMedium
import {{ cookiecutter.namespace }}.api.SessionStore
import {{ cookiecutter.namespace }}.api.model.LocalSessionData
import {{ cookiecutter.namespace }}.controller.ProgressController
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import {{ cookiecutter.namespace }}.ui.theme.dimensions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen() {
    val viewModel = hiltViewModel<LoginScreenViewModel>()

    val actions = LoginScreenActions(
        onLogin = viewModel::performLogin
    )

    LoginScreenContent(
        actions = actions
    )
}

private data class LoginScreenActions(
    val onLogin: () -> Unit = {}
)

@Composable
private fun LoginScreenContent(
    actions: LoginScreenActions
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
                text = "Please log in",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(MaterialTheme.dimensions.small)
            )
            ButtonMedium(
                onClick = actions.onLogin,
                text = "Login"
            )
        }
    }
}

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val sessionStore: SessionStore,
    private val progressController: ProgressController
) : ViewModel() {

    fun performLogin() {
        viewModelScope.launch {
            progressController.loadWithProgress {
                // TODO replace with a real login API call
                delay(1000.milliseconds)
                sessionStore.updateBearer(
                    LocalSessionData(
                        sessionToken = "demo-session-token",
                        refreshToken = "demo-refresh-token"
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreenContent(
            actions = LoginScreenActions()
        )
    }
}
