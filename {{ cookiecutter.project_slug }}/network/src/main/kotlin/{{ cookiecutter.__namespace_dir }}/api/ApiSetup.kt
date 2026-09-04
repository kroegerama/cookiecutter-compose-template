package {{ cookiecutter.namespace }}.api

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import arrow.core.getOrElse
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.kroegerama.kmp.kaiteki.formatting.asHumanReadableBytes
import {{ cookiecutter.namespace }}.api.model.ApiConfig
import dagger.hilt.android.qualifiers.ApplicationContext
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
import logcat.asLog
import logcat.logcat
import okhttp3.Cache
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiSetup @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiConfig: ApiConfig,
    private val sessionStore: SessionStore,
    private val chuckerInterceptor: ChuckerInterceptor
) {

    fun install() {
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
    ): HttpClient {
        val cacheDir = File(context.cacheDir, "okhttp_cache").apply { mkdirs() }
        val cache = Cache(
            directory = cacheDir,
            maxSize = diskCacheSize(cacheDir)
        )
        return HttpClient(OkHttp) {
            engine {
                config { cache(cache) }
                addInterceptor(chuckerInterceptor)
            }
            decorator()
        }
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

    private fun diskCacheSize(cacheDir: File): Long = try {
        val sm = context.getSystemService(StorageManager::class.java)
        val uuid = sm.getUuidForPath(cacheDir)
        val quota = sm.getCacheQuotaBytes(uuid)
        (quota / 4).coerceIn(MIN_SIZE, MAX_SIZE).also { size ->
            logcat { "diskCacheSize($cacheDir) = ${size.asHumanReadableBytes()}" }
        }
    } catch (e: Exception) {
        logcat { e.asLog() }
        MIN_SIZE
    }

    companion object {
        private const val MIN_SIZE = 16L * 1024 * 1024
        private const val MAX_SIZE = 256L * 1024 * 1024
    }
}
