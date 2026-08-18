package com.fankes.coloros.notify.ui.share

import androidx.compose.runtime.staticCompositionLocalOf
import top.yukonga.miuix.kmp.blur.LayerBackdrop

val LocalDarkTheme = staticCompositionLocalOf { false }

/** 顶栏是否处于毛玻璃透明态（由主壳提供）。 */
val LocalMiuixBlurActive = staticCompositionLocalOf { false }

/** 页面内容 LayerBackdrop，供液态玻璃底栏采样。 */
val LocalLayerBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }
