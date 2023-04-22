package kengine.util

actual fun String.format(vararg values: Any?) = String.format(this, *values)