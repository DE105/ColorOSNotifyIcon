package com.fankes.coloros.notify.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.fankes.coloros.notify.rules.IconRule
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.about.AboutScreen
import com.fankes.coloros.notify.ui.home.HomeScreen
import com.fankes.coloros.notify.ui.home.HomeScreenState
import com.fankes.coloros.notify.ui.rules.InstalledAppChoice
import com.fankes.coloros.notify.ui.rules.RuleNavigationHost
import com.fankes.coloros.notify.ui.rules.RuleListScreen
import com.fankes.coloros.notify.ui.rules.RuleListState
import com.fankes.coloros.notify.ui.theme.ThemeConfig
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.SnackbarHostState

@Composable
fun GlyphRoot(
    homeState: HomeScreenState,
    ruleState: RuleListState,
    themeConfig: ThemeConfig,
    onThemeConfigChange: (ThemeConfig) -> Unit,
    onSyncRules: ((String) -> Unit) -> Unit,
    onRestartSystemUi: ((String) -> Unit) -> Unit,
    onRulesEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onIconSourceModeChange: (RuleStore.IconSourceMode, (String) -> Unit) -> Unit,
    onPanelIconReplacementEnabledChange: (Boolean, (String) -> Unit) -> Unit,
    onLockScreenCapsuleIconReplacementEnabledChange: (Boolean, (String) -> Unit) -> Unit,
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
    var rulesIsRootNavigation by remember { mutableStateOf(true) }
    var settingsIsRootNavigation by remember { mutableStateOf(true) }
    fun showSnackbar(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }
    RuleNavigationHost(
        state = ruleState,
        onRuleIconSourceChange = onRuleIconSourceChange,
        onBindUnadaptedApp = onBindUnadaptedApp,
        snackbarHostState = snackbarHostState,
        onRootNavigationChange = { rulesIsRootNavigation = it },
    ) { onChooseIcon, onAddUnadaptedApp ->
        MainShell(
            home = MainTabSlot { bottomPadding ->
                HomeScreen(
                    state = homeState,
                    bottomPadding = bottomPadding,
                    snackbarHostState = snackbarHostState,
                    onSyncRules = onSyncRules,
                    onRestartSystemUi = onRestartSystemUi,
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
                    homeState = homeState,
                    themeConfig = themeConfig,
                    onThemeConfigChange = onThemeConfigChange,
                    onPredictiveBackChange = { enabled ->
                        onThemeConfigChange(themeConfig.copy(predictiveBackToHomeEnabled = enabled))
                    },
                    onRulesEnabledChange = { onRulesEnabledChange(it, ::showSnackbar) },
                    onIconSourceModeChange = { onIconSourceModeChange(it, ::showSnackbar) },
                    onPanelIconReplacementEnabledChange = {
                        onPanelIconReplacementEnabledChange(it, ::showSnackbar)
                    },
                    onLockScreenCapsuleIconReplacementEnabledChange = {
                        onLockScreenCapsuleIconReplacementEnabledChange(it, ::showSnackbar)
                    },
                    onOplusPushSpecialHandlingEnabledChange = {
                        onOplusPushSpecialHandlingEnabledChange(it, ::showSnackbar)
                    },
                    onPlaceholderIconEnabledChange = {
                        onPlaceholderIconEnabledChange(it, ::showSnackbar)
                    },
                    onLauncherIconHiddenChange = { onLauncherIconHiddenChange(it, ::showSnackbar) },
                    onRootNavigationChange = { settingsIsRootNavigation = it },
                )
            },
            snackbarHostState = snackbarHostState,
            rulesIsRootNavigation = rulesIsRootNavigation,
            settingsIsRootNavigation = settingsIsRootNavigation,
            themeConfig = themeConfig,
        )
    }
}
