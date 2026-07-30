package {{ cookiecutter.namespace }}.ui.theme

import android.graphics.Color
import androidx.activity.SystemBarStyle

val StatusBarStyle = SystemBarStyle.light(
    scrim = Color.TRANSPARENT,
    darkScrim = Color.TRANSPARENT
)

val NavigationBarStyle = SystemBarStyle.light(
    scrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
    darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
)
