package com.fankes.coloros.notify.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.ui.component.rememberMiuixPageBackdrop
import com.fankes.coloros.notify.ui.liquid.IosLiquidGlassNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.ListView

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
    val content: @Composable (bottomPadding: Dp) -> Unit,
)

/**
 * 一级页壳遵循 Mishka 的分层：这里只负责分页和底部导航，各分页自行持有 Miuix Scaffold 与顶栏。
 */
@Composable
fun MainShell(
    home: MainTabSlot,
    rules: MainTabSlot,
    about: MainTabSlot,
    snackbarHostState: SnackbarHostState,
    isRootNavigation: Boolean,
) {
    val tabs = MainTab.all
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val mainPagerState = rememberMainPagerState(pagerState)
    val slots = arrayOf(home, rules, about)
    val bottomBarBackdrop = rememberMiuixPageBackdrop()
    val navigationItems = tabs.map { tab ->
        NavigationItem(label = stringResource(tab.labelRes), icon = tab.icon)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect {
            mainPagerState.syncPage()
        }
    }

    MainScreenBackHandler(
        mainPagerState = mainPagerState,
        isRootNavigation = isRootNavigation,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            IosLiquidGlassNavigationBar(
                items = navigationItems,
                selectedIndex = mainPagerState.selectedPage.coerceIn(0, tabs.lastIndex),
                onItemClick = mainPagerState::animateToPage,
                backdrop = bottomBarBackdrop,
                isBlurActive = bottomBarBackdrop != null,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (bottomBarBackdrop != null) {
                        Modifier.layerBackdrop(bottomBarBackdrop)
                    } else {
                        Modifier
                    },
                ),
            state = pagerState,
            beyondViewportPageCount = 2,
        ) { index ->
            slots[index].content(padding.calculateBottomPadding())
        }
    }
}

/** 与 Mishka 一致：位于其他一级分页时，返回手势先回主页，而不是直接退出应用。 */
@Composable
private fun MainScreenBackHandler(
    mainPagerState: MainPagerState,
    isRootNavigation: Boolean,
) {
    val enabled by remember(mainPagerState, isRootNavigation) {
        derivedStateOf {
            isRootNavigation && mainPagerState.selectedPage != MainTab.Home.index
        }
    }
    val navigationEventState = rememberNavigationEventState(NavigationEventInfo.None)

    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = enabled,
        onBackCompleted = { mainPagerState.animateToPage(MainTab.Home.index) },
    )
}
