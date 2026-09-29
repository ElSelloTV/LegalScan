package ar.com.elsellotv.legalscan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NavyPrimary = Color(0xFF0B1F3A)
private val AccentBlue = Color(0xFF4FC3F7)

private val LightColors = lightColorScheme(
    primary = NavyPrimary,
    secondary = AccentBlue,
)

private val DarkColors = darkColorScheme(
    primary = AccentBlue,
    secondary = NavyPrimary,
)

@Composable
fun LegalScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
