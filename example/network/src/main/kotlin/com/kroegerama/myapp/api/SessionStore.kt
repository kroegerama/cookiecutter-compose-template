package com.kroegerama.myapp.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kroegerama.kmp.kaiteki.datastore.flow
import dagger.hilt.android.qualifiers.ApplicationContext
import com.kroegerama.myapp.api.model.LocalSessionData
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session-store",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

@Singleton
class SessionStore @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore = context.sessionDataStore

    val bearerFlow: Flow<BearerTokens?> = dataStore.flow { preferences ->
        val session = preferences[KEY_SESSION] ?: return@flow null
        val refresh = preferences[KEY_REFRESH] ?: return@flow null
        BearerTokens(
            accessToken = session,
            refreshToken = refresh
        )
    }

    val loggedInFlow: Flow<Boolean> = bearerFlow.map { it != null }.distinctUntilChanged()

    suspend fun getBearer(): BearerTokens? = bearerFlow.first()

    suspend fun updateBearer(sessionData: LocalSessionData): BearerTokens {
        logcat { sessionData.toString() }
        dataStore.edit { preferences ->
            preferences[KEY_SESSION] = sessionData.sessionToken
            preferences[KEY_REFRESH] = sessionData.refreshToken
        }
        return BearerTokens(
            accessToken = sessionData.sessionToken,
            refreshToken = sessionData.refreshToken
        )
    }

    suspend fun clearBearer() {
        // NonCancellable, so the token cache is always cleared, even if the caller's
        // scope gets cancelled as a consequence of the logged-out state emission
        withContext(NonCancellable) {
            dataStore.edit { preferences ->
                preferences.remove(KEY_SESSION)
                preferences.remove(KEY_REFRESH)
            }
            Api.client.authProvider<BearerAuthProvider>()?.clearToken()
        }
    }

    companion object {
        private val KEY_SESSION = stringPreferencesKey("session")
        private val KEY_REFRESH = stringPreferencesKey("refresh")
    }
}
