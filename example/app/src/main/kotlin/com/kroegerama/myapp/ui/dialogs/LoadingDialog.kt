package com.kroegerama.myapp.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kroegerama.kmp.kaiteki.compose.components.ButtonSmall
import com.kroegerama.myapp.ui.icons.AppIcons
import com.kroegerama.myapp.ui.icons.Cookie
import com.kroegerama.myapp.ui.theme.AppTheme
import com.kroegerama.myapp.ui.theme.dimensions
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoadingDialog(
    label: String? = null
) {
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusManager.clearFocus(true)
        softwareKeyboardController?.hide()
    }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.size(160.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.small)
            ) {
                Image(
                    imageVector = AppIcons.Cookie,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                label?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoadingDialogPreview() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            val inspection = LocalInspectionMode.current
            var show by remember { mutableStateOf(inspection) }

            LaunchedEffect(show) {
                if (show) {
                    delay(4000.milliseconds)
                    show = false
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Image(
                    imageVector = AppIcons.Cookie,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.padding(16.dp)
                )
                ButtonSmall(
                    onClick = { show = true },
                    text = "Show loading dialog"
                )
            }

            if (show) {
                LoadingDialog(
//                    label = "Loading…"
                )
            }
        }
    }
}
