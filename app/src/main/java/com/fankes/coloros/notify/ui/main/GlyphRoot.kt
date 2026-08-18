package com.fankes.coloros.notify.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.rules.IconRule
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.about.AboutScreen
import com.fankes.coloros.notify.ui.home.HomeScreen
import com.fankes.coloros.notify.ui.home.HomeScreenState
import com.fankes.coloros.notify.ui.rules.InstalledAppChoice
import com.fankes.coloros.notify.ui.rules.RuleListScreen
import com.fankes.coloros.notify.ui.rules.RuleListState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.SnackbarHostState

@Composable
fun GlyphRoot(
    homeState: HomeScreenState,
    ruleState: RuleListState,
    onSyncRules: ((String) -> Unit) -> Unit,
    onRestartSystemUi: ((String) -> Unit) -> Unit,
    onRulesEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onIconSourceModeChange: (RuleStore.IconSourceMode, (String) -> Unit) -> Unit,
    onPanelIconReplacementEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onOplusPushSpecialHandlingEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onPlaceholderIconEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onLauncherIconHiddenChange: (Boolean, (String) -> Unit) -> Unit,
    onQueryChange: (String) -> Unit,
    onRuleEnabledChange: (IconRule, Boolean, (String) -> Unit) -> Unit,
    onRuleEnabledAllChange: (IconRule, Boolean, (String) -> Unit) -> Unit,
    onInstalledRulesEnabledAllChange: (Boolean, (String) -> Unit) -> Unit,
    onRuleIconSourceChange: (IconRule, String?, (String) -> Unit) -> Unit,
    onBindUnadaptedApp: (InstalledAppChoice, String, (String) -> Unit) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    fun showSnackbar(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }
    val homeScroll = rememberTabScrollBehavior()
    val rulesScroll = rememberTabScrollBehavior()
    val aboutScroll = rememberTabScrollBehavior()

    MainShell(
        home = MainTabSlot(
            title = stringResource(R.string.app_name),
            largeTitle = stringResource(R.string.home_title),
            scrollBehavior = homeScroll,
        ) { padding ->
            HomeScreen(
                state = homeState,
                contentPadding = padding,
                scrollBehavior = homeScroll,
                snackbarHostState = snackbarHostState,
                onSyncRules = onSyncRules,
                onRestartSystemUi = onRestartSystemUi,
                onRulesEnabledChange = onRulesEnabledChange,
                onIconSourceModeChange = onIconSourceModeChange,
                onPanelIconReplacementEnabledChange = onPanelIconReplacementEnabledChange,
                onOplusPushSpecialHandlingEnabledChange = onOplusPushSpecialHandlingEnabledChange,
                onPlaceholderIconEnabledChange = onPlaceholderIconEnabledChange,
            )
        },
        rules = MainTabSlot(
            title = stringResource(R.string.rules_title),
            scrollBehavior = rulesScroll,
        ) { padding ->
            RuleListScreen(
                state = ruleState,
                onQueryChange = onQueryChange,
                onRuleEnabledChange = onRuleEnabledChange,
                onRuleEnabledAllChange = onRuleEnabledAllChange,
                onInstalledRulesEnabledAllChange = onInstalledRulesEnabledAllChange,
                onRuleIconSourceChange = onRuleIconSourceChange,
                onBindUnadaptedApp = onBindUnadaptedApp,
                contentPadding = padding,
                scrollBehavior = rulesScroll,
                snackbarHostState = snackbarHostState,
            )
        },
        about = MainTabSlot(
            title = stringResource(R.string.about_title),
            scrollBehavior = aboutScroll,
        ) { padding ->
            AboutScreen(
                contentPadding = padding,
                scrollBehavior = aboutScroll,
                launcherIconHidden = homeState.launcherIconHidden,
                onLauncherIconHiddenChange = { onLauncherIconHiddenChange(it, ::showSnackbar) },
            )
        },
        snackbarHostState = snackbarHostState,
    )
}
