package com.kroegerama.myapp.api.model

import io.ktor.http.Url

data class ApiConfig(
    val baseUrl: Url,
    val versionName: String,
    val versionCode: Int,
    val applicationId: String
)
