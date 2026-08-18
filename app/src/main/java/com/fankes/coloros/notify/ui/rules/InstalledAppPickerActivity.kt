package com.fankes.coloros.notify.ui.rules

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.theme.ColorOSNotifyIconTheme
import java.util.Locale
import java.util.concurrent.Executors

class InstalledAppPickerActivity : ComponentActivity() {

    private var apps by mutableStateOf<List<InstalledAppChoice>>(emptyList())
    private var pendingApp: InstalledAppChoice? = null
    private val loader = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "installed-app-picker-loader")
    }
    private val iconPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val app = pendingApp ?: return@registerForActivityResult
        pendingApp = null
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val sourcePackage = IconPickerActivity.resultSourcePackage(result.data) ?: return@registerForActivityResult
        setResult(
            Activity.RESULT_OK,
            Intent()
                .putExtra(EXTRA_RESULT_TARGET_PACKAGE, app.packageName)
                .putExtra(EXTRA_RESULT_SOURCE_PACKAGE, sourcePackage),
        )
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadApps()
        setContent {
            ColorOSNotifyIconTheme {
                InstalledAppPickerScreen(
                    apps = apps,
                    onSelect = { app ->
                        pendingApp = app
                        iconPickerLauncher.launch(
                            IconPickerActivity.createIntent(
                                context = this,
                                targetAppName = app.label,
                                targetPackageName = app.packageName,
                                currentSourcePackage = null,
                                canClear = false,
                            ),
                        )
                    },
                    onBack = ::finish,
                )
            }
        }
    }

    private fun loadApps() {
        loader.execute {
            val rulePackageNames = runCatching {
                RuleStore.rules.mapTo(linkedSetOf()) { it.packageName }
            }.getOrDefault(emptySet())
            val loadedApps = runCatching {
                packageManager
                    .getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS)
                    .asSequence()
                    .filter { it.packageName !in rulePackageNames }
                    .map { application ->
                        val label = runCatching {
                            packageManager.getApplicationLabel(application).toString()
                                .ifBlank { application.packageName }
                        }.getOrDefault(application.packageName)
                        InstalledAppChoice(packageName = application.packageName, label = label)
                    }
                    .sortedBy { it.label.lowercase(Locale.getDefault()) }
                    .toList()
            }.getOrDefault(emptyList())
            runOnUiThread {
                if (!isDestroyed) {
                    apps = loadedApps
                }
            }
        }
    }

    companion object {
        private const val EXTRA_RESULT_TARGET_PACKAGE = "result_target_package"
        private const val EXTRA_RESULT_SOURCE_PACKAGE = "result_source_package"

        fun createIntent(context: Context) = Intent(context, InstalledAppPickerActivity::class.java)

        fun resultTargetPackage(intent: Intent?): String? =
            intent?.getStringExtra(EXTRA_RESULT_TARGET_PACKAGE)

        fun resultSourcePackage(intent: Intent?): String? =
            intent?.getStringExtra(EXTRA_RESULT_SOURCE_PACKAGE)
    }
}
