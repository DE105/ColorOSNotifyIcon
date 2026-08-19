package com.fankes.coloros.notify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.fankes.coloros.notify.ui.share.LocalDarkTheme

@Composable
fun ColorOSNotifyIconTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val themeConfig = ThemeConfig(
        colorMode = when {
            darkTheme -> 2
            else -> 1
        },
    )
    androidx.compose.runtime.CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        GlyphAppTheme(themeConfig = themeConfig, content = content)
    }
}
