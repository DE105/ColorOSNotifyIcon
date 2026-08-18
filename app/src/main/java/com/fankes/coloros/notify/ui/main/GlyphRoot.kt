package com.fankes.coloros.notify.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.fankes.coloros.notify.rules.IconRule
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.about.AboutScreen
import com.fankes.coloros.notify.ui.home.HomeScreen
import com.fankes.coloros.notify.ui.home.HomeScreenState
import com.fankes.coloros.notify.ui.rules.InstalledAppChoice
import com.fankes.coloros.notify.ui.rules.RuleNavigationHost
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
    RuleNavigationHost(
        state = ruleState,
        onRuleIconSourceChange = onRuleIconSourceChange,
        onBindUnadaptedApp = onBindUnadaptedApp,
        snackbarHostState = snackbarHostState,
    ) { onChooseIcon, onAddUnadaptedApp, isRootNavigation ->
        MainShell(
            home = MainTabSlot { bottomPadding ->
                HomeScreen(
                    state = homeState,
                    bottomPadding = bottomPadding,
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
            rules = MainTabSlot { bottomPadding ->
                RuleListScreen(
                    state = ruleState,
                    onQueryChange = onQueryChange,
                    onRuleEnabledChange = onRuleEnabledChange,
                    onRuleEnabledAllChange = onRuleEnabledAllChange,
                    onInstalledRulesEnabledAllChange = onInstalledRulesEnabledAllChange,
                    bottomPadding = bottomPadding,
                    snackbarHostState = snackbarHostState,
                    onChooseIcon = onChooseIcon,
                    onAddUnadaptedApp = onAddUnadaptedApp,
                )
            },
            about = MainTabSlot { bottomPadding ->
                AboutScreen(
                    bottomPadding = bottomPadding,
                    launcherIconHidden = homeState.launcherIconHidden,
                    onLauncherIconHiddenChange = { onLauncherIconHiddenChange(it, ::showSnackbar) },
                )
            },
            snackbarHostState = snackbarHostState,
            isRootNavigation = isRootNavigation,
        )
    }
}
