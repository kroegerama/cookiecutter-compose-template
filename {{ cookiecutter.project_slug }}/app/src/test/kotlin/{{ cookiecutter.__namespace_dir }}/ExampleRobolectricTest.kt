package {{ cookiecutter.namespace }}

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test with Android resources, which will execute on the development machine (host).
 *
 * See [Robolectric documentation](https://robolectric.org).
 */
@RunWith(AndroidJUnit4::class)
class ExampleRobolectricTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun print_sdkInt() {
        val full = Build.VERSION.SDK_INT_FULL
        val major = Build.getMajorSdkVersion(full)
        val minor = Build.getMinorSdkVersion(full)
        println("Build SDK: $major.$minor")
    }

    @Test
    fun appLabel_resolvesFromResources() {
        val label = context.applicationInfo.loadLabel(context.packageManager).toString()
        assertEquals(context.getString(R.string.app_name), label)
    }

    @Test
    fun packageName_matchesApplicationId() {
        assertEquals(BuildConfig.APPLICATION_ID, context.packageName)
    }
}
