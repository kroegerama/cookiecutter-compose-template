package {{ cookiecutter.namespace }}

import android.app.Activity
import android.app.Application
import android.content.MutableContextWrapper
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Context that points at [MainActivity] while one exists and at the application otherwise.
 * Safe to hold in singletons and ViewModels; hand it to APIs that need an Activity context, such as Credential Manager.
 * The application fallback only prevents leaks, calls that need an Activity fail while no [MainActivity] is alive.
 */
@Singleton
class CurrentActivityContext @Inject constructor(
    private val application: Application
) : MutableContextWrapper(application), Application.ActivityLifecycleCallbacks {

    /** Registers for activity lifecycle updates. Call once from [App.onCreate]. */
    fun install() {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = track(activity)

    override fun onActivityResumed(activity: Activity) = track(activity)

    override fun onActivityDestroyed(activity: Activity) {
        if (baseContext === activity) baseContext = application
    }

    // third-party activities (Chucker, Play Services) must not become the base context
    private fun track(activity: Activity) {
        if (activity is MainActivity) baseContext = activity
    }

    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
