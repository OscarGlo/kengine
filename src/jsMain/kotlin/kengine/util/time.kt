package kengine.util

import kotlinx.browser.window

actual fun doubleTime() = window.performance.now() / 1000