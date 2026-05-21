package {{ cookiecutter.namespace }}

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import {{ cookiecutter.namespace }}.api.ApiInitializer
import {{ cookiecutter.namespace }}.controller.LogoutHandler

class AppInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppInitializerEntryPoint {
        fun getLogoutHandler(): LogoutHandler
    }

    private lateinit var logoutHandler: LogoutHandler

    override fun create(context: Context) {
        val accessor = EntryPointAccessors.fromApplication<AppInitializerEntryPoint>(context)
        logoutHandler = accessor.getLogoutHandler()

        logoutHandler.init()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        ApiInitializer::class.java
    )
}
