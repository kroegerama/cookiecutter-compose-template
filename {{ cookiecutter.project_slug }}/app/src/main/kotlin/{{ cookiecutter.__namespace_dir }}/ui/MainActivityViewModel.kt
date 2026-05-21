package {{ cookiecutter.namespace }}.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import {{ cookiecutter.namespace }}.controller.ProgressController
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val progressController: ProgressController
) : ViewModel() {
    val loading = progressController.loading
}
