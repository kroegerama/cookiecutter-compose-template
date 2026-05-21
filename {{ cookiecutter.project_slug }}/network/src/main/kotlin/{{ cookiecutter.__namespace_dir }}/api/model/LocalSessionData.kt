package {{ cookiecutter.namespace }}.api.model

data class LocalSessionData(
    val sessionToken: String,
    val refreshToken: String
)
