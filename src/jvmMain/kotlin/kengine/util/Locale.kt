package kengine.util

import java.util.Locale as JvmLocale

actual val Locale.Companion.current: Locale
    get() = getOrCreate(JvmLocale.getDefault().toString())