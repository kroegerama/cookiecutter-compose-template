package {{ cookiecutter.namespace }}.api

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageClient

@Module
@InstallIn(SingletonComponent::class)
object ImageClientModule {

    @Provides
    @Singleton
    @ImageClient
    fun provideImageClient(
        chuckerInterceptor: ChuckerInterceptor
    ): HttpClient = HttpClient(OkHttp) {
        engine {
            addInterceptor(chuckerInterceptor)
        }
    }
}
