package no.victoria.hitsterapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HitsterPink,
    onPrimary = Color.White,
    secondary = HitsterTeal,
    onSecondary = HitsterDeepPurple,
    tertiary = HitsterBlue,
    background = HitsterDeepPurple,
    onBackground = HitsterText,
    surface = HitsterSurface,
    onSurface = HitsterText,
    surfaceVariant = HitsterSurfaceBright,
    onSurfaceVariant = HitsterMutedText,
    outline = HitsterMutedText
)

private val LightColorScheme = lightColorScheme(
    primary = HitsterMagenta,
    onPrimary = Color.White,
    secondary = HitsterTeal,
    onSecondary = HitsterDeepPurple,
    tertiary = HitsterBlue,
    background = Color(0xFFFFF7FF),
    onBackground = HitsterDeepPurple,
    surface = Color.White,
    onSurface = HitsterDeepPurple,
    surfaceVariant = Color(0xFFF4E6F8),
    onSurfaceVariant = HitsterPurple,
    outline = Color(0xFF7B5A87)
)

@Composable
fun HitsterAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme || !dynamicColor) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
