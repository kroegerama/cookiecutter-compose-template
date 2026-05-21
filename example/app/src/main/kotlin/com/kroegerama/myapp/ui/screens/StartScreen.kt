package com.kroegerama.myapp.ui.screens

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
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kroegerama.myapp.controller.ProgressController
import com.kroegerama.myapp.ui.navigation.Navigator
import com.kroegerama.myapp.ui.navigation.RootNavKey
import com.kroegerama.myapp.ui.scaffold.SnackbarController
import com.kroegerama.myapp.ui.theme.AppTheme
import com.kroegerama.myapp.ui.theme.dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun StartScreen(
    navigator: Navigator
) {
    val viewModel = hiltViewModel<StartScreenViewModel>()

    val actions = StartScreenActions(
        onNavigate = navigator::navigate,
        onProgress = viewModel::performProgress
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
    val onProgress: () -> Unit = {}
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
        }
    }
}

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val progressController: ProgressController,
    private val snackbarController: SnackbarController
) : ViewModel() {

    var uiState by mutableStateOf(StartScreenUiState("Hello App"))

    fun performProgress() {
        viewModelScope.launch {
            progressController.loadWithProgress {
                delay(2000)
                snackbarController.showSuccess("Success!!!")
            }
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
