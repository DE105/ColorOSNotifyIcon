package com.fankes.coloros.notify.ui.rules

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fankes.coloros.notify.rules.IconRule
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.theme.ColorOSNotifyIconTheme
import java.util.concurrent.Executors

class IconPickerActivity : ComponentActivity() {

    private var libraryRules by mutableStateOf<List<IconRule>>(emptyList())
    private val loader = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "icon-picker-loader")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadRules()
        val targetAppName = intent.getStringExtra(EXTRA_TARGET_APP_NAME).orEmpty()
        val targetPackageName = intent.getStringExtra(EXTRA_TARGET_PACKAGE_NAME).orEmpty()
        val currentSourcePackage = intent.getStringExtra(EXTRA_CURRENT_SOURCE_PACKAGE)
        val canClear = intent.getBooleanExtra(EXTRA_CAN_CLEAR, false)
        setContent {
            ColorOSNotifyIconTheme {
                IconPickerScreen(
                    targetAppName = targetAppName,
                    targetPackageName = targetPackageName,
                    currentSourcePackage = currentSourcePackage,
                    libraryRules = libraryRules,
                    canClear = canClear,
                    onSelect = { rule ->
                        setResult(
                            Activity.RESULT_OK,
                            Intent().putExtra(EXTRA_RESULT_SOURCE_PACKAGE, rule.packageName),
                        )
                        finish()
                    },
                    onClear = {
                        setResult(
                            Activity.RESULT_OK,
                            Intent().putExtra(EXTRA_RESULT_CLEARED, true),
                        )
                        finish()
                    },
                    onBack = ::finish,
                )
            }
        }
    }

    private fun loadRules() {
        loader.execute {
            val rules = runCatching { RuleStore.rules.filter(IconRule::isLibraryEntry) }
                .getOrDefault(emptyList())
            runOnUiThread {
                if (!isDestroyed) {
                    libraryRules = rules
                }
            }
        }
    }

    companion object {
        private const val EXTRA_TARGET_APP_NAME = "target_app_name"
        private const val EXTRA_TARGET_PACKAGE_NAME = "target_package_name"
        private const val EXTRA_CURRENT_SOURCE_PACKAGE = "current_source_package"
        private const val EXTRA_CAN_CLEAR = "can_clear"
        private const val EXTRA_RESULT_SOURCE_PACKAGE = "result_source_package"
        private const val EXTRA_RESULT_CLEARED = "result_cleared"

        fun createIntent(
            context: Context,
            targetAppName: String,
            targetPackageName: String,
            currentSourcePackage: String?,
            canClear: Boolean,
        ) = Intent(context, IconPickerActivity::class.java).apply {
            putExtra(EXTRA_TARGET_APP_NAME, targetAppName)
            putExtra(EXTRA_TARGET_PACKAGE_NAME, targetPackageName)
            putExtra(EXTRA_CURRENT_SOURCE_PACKAGE, currentSourcePackage)
            putExtra(EXTRA_CAN_CLEAR, canClear)
        }

        fun resultSourcePackage(intent: Intent?): String? =
            intent?.getStringExtra(EXTRA_RESULT_SOURCE_PACKAGE)

        fun resultWasCleared(intent: Intent?): Boolean =
            intent?.getBooleanExtra(EXTRA_RESULT_CLEARED, false) == true
    }
}
