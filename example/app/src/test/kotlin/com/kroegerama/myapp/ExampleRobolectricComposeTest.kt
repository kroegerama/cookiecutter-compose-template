package com.kroegerama.myapp

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
import com.kroegerama.myapp.ui.components.Checkbox
import com.kroegerama.myapp.ui.theme.AppTheme
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example Compose UI test, which will execute on the development machine (host).
 *
 * See [Compose testing documentation](https://developer.android.com/develop/ui/compose/testing).
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class ExampleRobolectricComposeTest {

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
