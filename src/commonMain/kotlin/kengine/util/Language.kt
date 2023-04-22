package kengine.util

class Language {
    companion object {
        private val languages = mutableMapOf<Locale, Language>()

        suspend fun load(locale: Locale, res: Resource) {
            res.waitForLoad()

            languages
                .getOrPut(locale) { Language() }
                .translations.putAll(
                    res.getText().split("").associate {
                        val line = it.split(Regex("="), 1)
                        line[0] to line[1]
                    }
                )
        }

        operator fun get(key: String) = getOrDefault(key)

        fun getOrNull(key: String) = languages[Locale.current]?.translations?.get(key)

        fun getOrDefault(key: String, default: String = key) = getOrNull(key) ?: default
    }

    val translations = mutableMapOf<String, String>()
}