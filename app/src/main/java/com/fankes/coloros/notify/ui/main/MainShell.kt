package com.fankes.coloros.notify.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.ui.liquid.IosLiquidGlassNavigationBar
import com.fankes.coloros.notify.ui.share.LocalLayerBackdrop
import com.fankes.coloros.notify.ui.share.LocalMiuixBlurActive
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.ListView
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class MainTab(
    val index: Int,
    val labelRes: Int,
    val icon: ImageVector,
) {
    Home(0, R.string.tab_home, MiuixIcons.Home),
    Rules(1, R.string.tab_rules, MiuixIcons.ListView),
    About(2, R.string.tab_about, MiuixIcons.Info),
    ;

    companion object {
        val all = arrayOf(Home, Rules, About)
    }
}

class MainTabSlot(
    val title: String,
    val largeTitle: String = title,
    val scrollBehavior: top.yukonga.miuix.kmp.basic.ScrollBehavior,
    val actions: @Composable RowScope.() -> Unit = {},
    val content: @Composable (PaddingValues) -> Unit,
)

/**
 * 一级页壳：MIUIX Scaffold + 单一 layerBackdrop + 悬浮液态玻璃底栏。
 *
 * 顶栏必须走 Scaffold.topBar + textureBlur；内容区只挂一个 backdrop。
 * 切勿把顶栏拆出 Scaffold，也勿给每个 Pager 页各挂 backdrop。
 */
@Composable
fun MainShell(
    home: MainTabSlot,
    rules: MainTabSlot,
    about: MainTabSlot,
    snackbarHostState: SnackbarHostState,
) {
    val tabs = MainTab.all
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val homePager = rememberMainPagerState(pagerState)
    val slots = arrayOf(home, rules, about)
    val selectedIndex = homePager.selectedPage
    val settled = pagerState.settledPage
    val currentSlot = slots[settled]

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect {
            homePager.syncPage()
        }
    }

    val blurActive = isRuntimeShaderSupported()
    val surfaceColor = MiuixTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val listBottomSpace = 112.dp + navInset
    val barItems = tabs.map { tab ->
        NavigationItem(label = stringResource(tab.labelRes), icon = tab.icon)
    }

    CompositionLocalProvider(LocalLayerBackdrop provides backdrop) {
        Scaffold(
            topBar = {
                BlurredTopBar(blurActive = blurActive, backdrop = backdrop) {
                    TopAppBar(
                        title = currentSlot.title,
                        largeTitle = currentSlot.largeTitle,
                        color = if (blurActive) Color.Transparent else surfaceColor,
                        actions = currentSlot.actions,
                        scrollBehavior = currentSlot.scrollBehavior,
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {},
            content = { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (blurActive) Modifier.layerBackdrop(backdrop) else Modifier),
                    ) {
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            beyondViewportPageCount = 2,
                        ) { index ->
                            slots[index].content(
                                PaddingValues(
                                    top = padding.calculateTopPadding(),
                                    bottom = listBottomSpace,
                                )
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    ) {
                        IosLiquidGlassNavigationBar(
                            items = barItems,
                            selectedIndex = selectedIndex.coerceIn(0, tabs.lastIndex),
                            onItemClick = { index -> homePager.animateToPage(index) },
                            backdrop = backdrop,
                            isBlurActive = blurActive,
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun rememberTabScrollBehavior() = MiuixScrollBehavior(rememberTopAppBarState())

@Composable
private fun BlurredTopBar(
    blurActive: Boolean,
    backdrop: top.yukonga.miuix.kmp.blur.LayerBackdrop,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalMiuixBlurActive provides blurActive) {
        Box(
            modifier = if (blurActive) {
                Modifier.textureBlur(
                    backdrop = backdrop,
                    shape = RectangleShape,
                    blurRadius = 25f,
                    colors = BlurColors(
                        blendColors = listOf(
                            BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(alpha = 0.87f)),
                        ),
                    ),
                )
            } else {
                Modifier
            },
        ) {
            content()
        }
    }
}
