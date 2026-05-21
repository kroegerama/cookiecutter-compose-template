package {{ cookiecutter.namespace }}

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DependencyProvider {

    @Provides
    @ProcessLifecycleOwner
    fun provideProcessLifecycleOwner(): LifecycleOwner = androidx.lifecycle.ProcessLifecycleOwner.get()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProcessLifecycleOwner

@Module
@InstallIn(ActivityComponent::class)
object ComponentActivityModule {

    @Provides
    @ActivityScoped
    fun provideComponentActivity(activity: Activity): ComponentActivity = activity as ComponentActivity
}
