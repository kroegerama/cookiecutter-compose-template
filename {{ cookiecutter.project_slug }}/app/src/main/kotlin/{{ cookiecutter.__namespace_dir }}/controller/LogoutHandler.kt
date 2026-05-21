package {{ cookiecutter.namespace }}.controller

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import {{ cookiecutter.namespace }}.ProcessLifecycleOwner
import {{ cookiecutter.namespace }}.api.SessionStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutHandler @Inject constructor(
    private val dataStore: DataStore,
    private val sessionStore: SessionStore,
    @ProcessLifecycleOwner
    private val lifecycleOwner: LifecycleOwner
) {
    private var job: Job? = null

    fun init() {
        job?.cancel()
        job = lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionStore.loggedInFlow.collect { loggedIn ->
                    if (!loggedIn) {
                        dataStore.clear()
                    }
                }
            }
        }
    }
}
