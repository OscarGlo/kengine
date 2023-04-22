package kengine.util

class Locale(val code: String) {
    val parts = code.split(Regex("[-_]"))

    init {
        locales.add(this)
    }

    companion object {
        val locales = mutableListOf<Locale>()

        operator fun get(code: String) = Locale(code).let { loc ->
            locales.firstOrNull { it.parts.array().contentEquals(loc.parts.array()) }
        }

        fun fallback(code: String): Locale? {
            val parts = Locale(code).parts.toMutableList()

            while (parts.isNotEmpty()) {
                val locale = locales.firstOrNull { it.parts.containsAll(parts) }
                if (locale != null)
                    return locale
                parts.removeLast()
            }

            return null
        }

        fun getOrCreate(code: String) = get(code) ?: Locale(code)
    }
}

expect val Locale.Companion.current: Locale