package {{ cookiecutter.namespace }}.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import {{ cookiecutter.namespace }}.BuildConfig
import {{ cookiecutter.namespace }}.api.model.ApiConfig
import io.ktor.http.Url
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideApiConfig(
        flavorConfig: FlavorConfig
    ): ApiConfig = ApiConfig(
        baseUrl = flavorConfig.baseUrl,
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        applicationId = BuildConfig.APPLICATION_ID
    )

    @Provides
    @Singleton
    fun provideFlavorConfig(): FlavorConfig = FlavorConfigImpl
}

interface FlavorConfig {
    val baseUrl: Url
}

// TODO create one impl per Flavor in flavor source folder
data object FlavorConfigImpl : FlavorConfig {
    override val baseUrl: Url = Url("todo")
}
