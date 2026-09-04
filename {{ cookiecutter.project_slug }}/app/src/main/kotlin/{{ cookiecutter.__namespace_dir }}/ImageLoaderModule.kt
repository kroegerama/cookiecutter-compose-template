package {{ cookiecutter.namespace }}

import android.content.Context
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.useExistingImageAsPlaceholder
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import {{ cookiecutter.namespace }}.api.ImageClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

    @OptIn(ExperimentalCoilApi::class)
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @ImageClient imageClient: HttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .crossfade(700)
        .useExistingImageAsPlaceholder(true)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.25)
                .build()
        }
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { imageClient }))
        }
        .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
        .build()
}
