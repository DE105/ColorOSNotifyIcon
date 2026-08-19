package com.fankes.coloros.notify

import android.app.Application
import com.fankes.coloros.notify.framework.RemoteConfigCoordinator
import com.fankes.coloros.notify.framework.XposedServiceBridge
import com.fankes.coloros.notify.rules.RuleStore
import com.fankes.coloros.notify.ui.theme.applyPredictiveBackCallbackEnabled
import com.fankes.coloros.notify.ui.theme.readThemeConfig
import io.github.libxposed.service.XposedService

class ColorOSNotifyIconApplication : Application() {

    private val configPublisher = object : XposedServiceBridge.Listener {
        override fun onServiceChanged(service: XposedService?) {
            service?.let { RemoteConfigCoordinator.publish(it) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        applyPredictiveBackCallbackEnabled(
            context = this,
            enabled = readThemeConfig(this).predictiveBackToHomeEnabled,
        )
        RuleStore.initialize(this)
        XposedServiceBridge.initialize()
        XposedServiceBridge.addListener(configPublisher)
    }
}
