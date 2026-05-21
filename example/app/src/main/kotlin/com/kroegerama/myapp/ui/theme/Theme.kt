package com.kroegerama.myapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val dimensions = DimensionValues()
    CompositionLocalProvider(
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = Shapes,
            typography = Typography,
            content = content
        )
    }
}
