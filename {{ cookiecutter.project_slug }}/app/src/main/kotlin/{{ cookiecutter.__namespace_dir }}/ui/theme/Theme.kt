package {{ cookiecutter.namespace }}.ui.theme

import androidx.compose.material3.MaterialExpressiveTheme
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
        MaterialExpressiveTheme(
            colorScheme = LightColorScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = Shapes,
            typography = Typography,
            content = content
        )
    }
}
