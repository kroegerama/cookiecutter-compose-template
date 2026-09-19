package {{ cookiecutter.namespace }}

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import {{ cookiecutter.namespace }}.ui.components.Checkbox
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example Compose UI test, which will execute on an Android device.
 *
 * See [Compose testing documentation](https://developer.android.com/develop/ui/compose/testing).
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class ExampleComposeTest {

    @Test
    fun print_sdkInt() {
        val full = Build.VERSION.SDK_INT_FULL
        val major = Build.getMajorSdkVersion(full)
        val minor = Build.getMinorSdkVersion(full)
        println("Build SDK: $major.$minor")
    }

    @Test
    fun checkbox_togglesOnClick() = runComposeUiTest {
        setContent {
            var checked by remember { mutableStateOf(false) }
            AppTheme {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    label = "Accept"
                )
            }
        }

        onNodeWithText("Accept").assertIsDisplayed()
        onNodeWithText("Accept").assertIsOff()
        onNodeWithText("Accept").performClick()
        onNodeWithText("Accept").assertIsOn()
    }
}
