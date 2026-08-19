package com.fankes.coloros.notify.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.home.HomeScreenState
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.preference.OverlaySpinnerPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference

@Composable
fun ModuleSettingsSection(
    state: HomeScreenState,
    onIconSourceModeChange: (RuleStore.IconSourceMode) -> Unit,
    onRulesEnabledChange: (Boolean) -> Unit,
    onPanelIconReplacementEnabledChange: (Boolean) -> Unit,
    onOplusPushSpecialHandlingEnabledChange: (Boolean) -> Unit,
    onPlaceholderIconEnabledChange: (Boolean) -> Unit,
) {
    val canEditConfig = state.canEditConfig
    val ruleLibraryMode = state.config.iconSourceMode == RuleStore.IconSourceMode.RuleLibrary
    IconSourceRow(state = state, onIconSourceModeChange = onIconSourceModeChange)
    ToggleComponent(
        title = stringResource(R.string.label_icon_enhancement_enabled),
        checked = state.config.rulesEnabled,
        enabled = canEditConfig,
        onCheckedChange = onRulesEnabledChange,
    )
    ToggleComponent(
        title = stringResource(R.string.label_panel_icon_replacement_enabled),
        checked = state.config.panelIconReplacementEnabled,
        enabled = canEditConfig,
        onCheckedChange = onPanelIconReplacementEnabledChange,
    )
    if (ruleLibraryMode) {
        ToggleComponent(
            title = stringResource(R.string.label_oplus_push_special_handling_enabled),
            checked = state.config.oplusPushSpecialHandlingEnabled,
            enabled = canEditConfig && state.config.rulesEnabled,
            onCheckedChange = onOplusPushSpecialHandlingEnabledChange,
        )
        ToggleComponent(
            title = stringResource(R.string.label_placeholder_icon_enabled),
            checked = state.config.placeholderIconEnabled,
            enabled = canEditConfig && state.config.rulesEnabled,
            onCheckedChange = onPlaceholderIconEnabledChange,
        )
    }
}

@Composable
private fun IconSourceRow(
    state: HomeScreenState,
    onIconSourceModeChange: (RuleStore.IconSourceMode) -> Unit,
) {
    val canEditConfig = state.canEditConfig
    val current = state.config.iconSourceMode
    val modes = RuleStore.IconSourceMode.entries
    val selectedIndex = modes.indexOf(current).coerceAtLeast(0)
    val ruleLibraryTitle = stringResource(R.string.label_icon_source_rule_library)
    val ruleLibrarySummary = stringResource(R.string.label_icon_source_rule_library_summary)
    val desktopThemeTitle = stringResource(R.string.label_icon_source_desktop_theme)
    val desktopThemeSummary = stringResource(R.string.label_icon_source_desktop_theme_summary)
    val items = listOf(
        DropdownItem(text = ruleLibraryTitle, summary = ruleLibrarySummary),
        DropdownItem(text = desktopThemeTitle, summary = desktopThemeSummary),
    )
    OverlaySpinnerPreference(
        title = stringResource(R.string.label_icon_source_mode),
        items = items,
        selectedIndex = selectedIndex,
        enabled = canEditConfig,
        onSelectedIndexChange = { index ->
            val mode = modes[index]
            if (mode != current) onIconSourceModeChange(mode)
        },
    )
}

@Composable
private fun ToggleComponent(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    SwitchPreference(
        title = title,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
    )
}
