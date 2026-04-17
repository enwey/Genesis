package com.genesis.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GenesisColorScheme = lightColorScheme(
    primary = Color(0xFF00B4FF), // 淡藍色發光
    secondary = Color(0xFF7000FF), // 符文紫
    background = Color(0xFFFFFFFF), // 純白宇宙
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1B1F),
    outline = Color(0xFFD1D1D1)
)

@Composable
fun GenesisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GenesisColorScheme,
        typography = Typography,
        content = content
    )
}
