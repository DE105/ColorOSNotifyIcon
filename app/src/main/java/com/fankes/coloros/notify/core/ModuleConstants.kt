package com.fankes.coloros.notify.core

object SystemPackages {
    const val SYSTEM_SCOPE = "system"
    const val SYSTEM_UI = "com.android.systemui"
}

object ModuleInfo {
    const val LOG_TAG = "ColorOSNotifyIcon"
    const val PROJECT_URL = "https://github.com/Mangi-11/Glyph"
    const val RELEASES_PAGE = "$PROJECT_URL/releases"
    const val RELEASES_API = "https://api.github.com/repos/Mangi-11/Glyph/releases/latest"
    const val RULES_BASE_URL = "https://raw.githubusercontent.com/fankes/AndroidNotifyIconAdapt/main"
    private const val RULES_CDN_BASE_URL = "https://cdn.jsdelivr.net/gh/fankes/AndroidNotifyIconAdapt@main"
    const val RULES_OS_URL = "$RULES_BASE_URL/OS/ColorOS/NotifyIconsSupportConfig.json"
    const val RULES_APP_URL = "$RULES_BASE_URL/APP/NotifyIconsSupportConfig.json"
    val RULES_OS_URLS = listOf(
        RULES_OS_URL,
        "$RULES_CDN_BASE_URL/OS/ColorOS/NotifyIconsSupportConfig.json",
    )
    val RULES_APP_URLS = listOf(
        RULES_APP_URL,
        "$RULES_CDN_BASE_URL/APP/NotifyIconsSupportConfig.json",
    )
}
