package com.kroegerama.myapp.api

import android.content.Context
import android.os.Build
import androidx.startup.Initializer
import arrow.core.getOrElse
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.kroegerama.myapp.api.model.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequest
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import logcat.logcat

class ApiInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApiInitializerEntryPoint {
        fun getApiConfig(): ApiConfig
        fun getSessionStore(): SessionStore
    }

    private lateinit var context: Context
    private lateinit var apiConfig: ApiConfig
    private lateinit var sessionStore: SessionStore

    override fun create(context: Context) {
        val accessor = EntryPointAccessors.fromApplication<ApiInitializerEntryPoint>(context)
        this.context = context
        apiConfig = accessor.getApiConfig()
        sessionStore = accessor.getSessionStore()

        Api.baseUrl = apiConfig.baseUrl

        Api.updateClient(
            withLogging = true,
            createHttpClient = ::createHttpClient
        ) {
            defaultRequest {
                headers {
                    append("app-version", apiConfig.versionName)
                    append("app-version-code", apiConfig.versionCode.toString())
                    append("app-id", apiConfig.applicationId)
                    append("device-os-release", Build.VERSION.RELEASE)
                }
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        sessionStore.getBearer()
                    }
                    refreshTokens {
                        refreshSession(oldTokens)
                    }
                }
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    if (cause is ClientRequestException) {
                        handleClientRequestException(cause, request)
                    }
                }
            }
        }
    }

    private suspend fun handleClientRequestException(cause: ClientRequestException, request: HttpRequest) {
        val response = cause.response
        logcat { "handleClientRequestException> $cause" }
    }

    private fun createHttpClient(
        decorator: HttpClientConfig<OkHttpConfig>.() -> Unit
    ) = HttpClient(OkHttp) {
        engine {
            val chuckerCollector = ChuckerCollector(
                context = context,
                showNotification = false
            )
            val chuckerInterceptor = ChuckerInterceptor.Builder(context)
                .collector(chuckerCollector)
                .alwaysReadResponseBody(true)
                .build()
            addInterceptor(chuckerInterceptor)
        }
        decorator()
    }

    private suspend fun RefreshTokensParams.refreshSession(oldTokens: BearerTokens?): BearerTokens? {
        val refreshToken = oldTokens?.refreshToken ?: return null
        val sessionData = AuthRepository.refreshSession(
            refreshToken = refreshToken
        ) {
            markAsRefreshTokenRequest()
        }.getOrElse {
            logcat { "refresh error $it" }
            sessionStore.clearBearer()
            return null
        }.data
        return sessionStore.updateBearer(sessionData)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

}
