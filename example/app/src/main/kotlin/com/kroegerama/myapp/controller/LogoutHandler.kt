package com.kroegerama.myapp.controller

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kroegerama.myapp.ProcessLifecycleOwner
import com.kroegerama.myapp.api.SessionStore
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

    fun start() {
        job?.cancel()
        job = lifecycleOwner.lifecycleScope.launch {
            sessionStore.loggedInFlow.collect { loggedIn ->
                if (!loggedIn) {
                    dataStore.clear()
                }
            }
        }
    }
}
