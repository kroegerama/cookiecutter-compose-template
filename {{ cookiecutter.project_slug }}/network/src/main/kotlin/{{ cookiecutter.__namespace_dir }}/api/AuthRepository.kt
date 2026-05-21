package {{ cookiecutter.namespace }}.api

import arrow.core.Either
import com.kroegerama.openapi.kmp.gen.companion.CallException
import com.kroegerama.openapi.kmp.gen.companion.HttpCallResponse
import {{ cookiecutter.namespace }}.api.model.LocalSessionData
import io.ktor.client.request.HttpRequestBuilder

object AuthRepository {

    suspend fun refreshSession(
        refreshToken: String,
        decorator: HttpRequestBuilder.() -> Unit = {}
    ): Either<CallException, HttpCallResponse<LocalSessionData>> {
        TODO()
    }

}
