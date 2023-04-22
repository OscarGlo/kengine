package kengine.util

import kotlinx.browser.window

actual val Locale.Companion.current
    get() = getOrCreate(window.navigator.language)