package {{ cookiecutter.namespace }}.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import {{ cookiecutter.namespace }}.api.SessionStore
import {{ cookiecutter.namespace }}.controller.ProgressController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    progressController: ProgressController,
    sessionStore: SessionStore
) : ViewModel() {
    val loading = progressController.loading.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val busy = progressController.busy.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    // null = session state not yet loaded; the splash screen stays visible until it resolves
    val loggedIn: StateFlow<Boolean?> = sessionStore.loggedInFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
}
