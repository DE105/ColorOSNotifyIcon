package com.fankes.coloros.notify.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fankes.coloros.notify.BuildConfig
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.core.ModuleInfo
import com.fankes.coloros.notify.ui.component.MiuixBlurredTopBar
import com.fankes.coloros.notify.ui.component.PreferenceGroup
import com.fankes.coloros.notify.ui.component.rememberMiuixPageBackdrop
import com.fankes.coloros.notify.update.UpdateChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun AboutScreen(
    bottomPadding: Dp,
    launcherIconHidden: Boolean,
    onLauncherIconHiddenChange: (Boolean) -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val backdrop = rememberMiuixPageBackdrop()
    val surfaceColor = MiuixTheme.colorScheme.surface
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var checkingUpdate by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateHasNew by remember { mutableStateOf(false) }
    var updateMessage by remember { mutableStateOf("") }
    var updateReleaseUrl by remember { mutableStateOf(ModuleInfo.RELEASES_PAGE) }

    Scaffold(
        topBar = {
            MiuixBlurredTopBar(backdrop) {
                TopAppBar(
                    title = stringResource(R.string.about_title),
                    color = if (backdrop != null) Color.Transparent else surfaceColor,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = maxOf(bottomPadding, innerPadding.calculateBottomPadding()),
            ),
        ) {
            item {
                PreferenceGroup {
                    BasicComponent(
                        title = stringResource(R.string.app_name),
                        summary = stringResource(
                            R.string.about_version,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE,
                        ),
                    )
                    BasicComponent(title = stringResource(R.string.app_description))
                }
            }
            item {
                PreferenceGroup {
                    SwitchPreference(
                        title = stringResource(R.string.label_hide_launcher_icon),
                        checked = launcherIconHidden,
                        onCheckedChange = onLauncherIconHiddenChange,
                    )
                }
            }
            item {
                PreferenceGroup {
                    ArrowPreference(
                        title = stringResource(R.string.about_project),
                        summary = stringResource(R.string.about_project_summary),
                        onClick = { context.openUrl(ModuleInfo.PROJECT_URL) },
                    )
                    ArrowPreference(
                        title = stringResource(R.string.about_check_update),
                        summary = stringResource(
                            if (checkingUpdate) {
                                R.string.about_check_update_checking
                            } else {
                                R.string.about_check_update_summary
                            },
                        ),
                        onClick = {
                            if (checkingUpdate) return@ArrowPreference
                            checkingUpdate = true
                            scope.launch {
                                val result = withContext(Dispatchers.IO) { UpdateChecker.check() }
                                checkingUpdate = false
                                updateHasNew = result.hasUpdate
                                updateMessage = result.message
                                updateReleaseUrl = result.releaseUrl
                                showUpdateDialog = true
                            }
                        },
                    )
                }
            }
            item {
                PreferenceGroup {
                    ArrowPreference(
                        title = stringResource(R.string.about_license),
                        summary = stringResource(R.string.about_license_agpl),
                        onClick = {
                            context.openUrl("https://www.gnu.org/licenses/agpl-3.0.html")
                        },
                    )
                    ArrowPreference(
                        title = stringResource(R.string.about_upstream),
                        summary = stringResource(R.string.about_upstream_fankes),
                        onClick = {
                            context.openUrl("https://github.com/fankes/ColorOSNotifyIcon")
                        },
                    )
                    ArrowPreference(
                        title = stringResource(R.string.about_rules_source),
                        summary = stringResource(R.string.about_rules_source_adapt),
                        onClick = {
                            context.openUrl("https://github.com/fankes/AndroidNotifyIconAdapt")
                        },
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    OverlayDialog(
        show = showUpdateDialog,
        title = stringResource(R.string.about_update_dialog_title),
        onDismissRequest = { showUpdateDialog = false },
    ) {
        Text(
            text = updateMessage,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .verticalScroll(rememberScrollState()),
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (updateHasNew) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = stringResource(R.string.about_update_ok),
                    onClick = { showUpdateDialog = false },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = stringResource(R.string.about_update_open),
                    onClick = {
                        showUpdateDialog = false
                        context.openUrl(updateReleaseUrl)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        } else {
            TextButton(
                text = stringResource(R.string.about_update_ok),
                onClick = { showUpdateDialog = false },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}

private fun android.content.Context.openUrl(url: String) {
    runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
