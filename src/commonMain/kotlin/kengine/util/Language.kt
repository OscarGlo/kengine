package kengine.util

class Language {
    companion object {
        private val languages = mutableMapOf<Locale, Language>()

        suspend fun load(locale: Locale, res: Resource) {
            res.waitForLoad()

            val translations = languages.getOrPut(locale) { Language() }.translations

            res.getText().split("").forEach {
                val line = it.split(Regex("="), 1)

                if (line.size > 1)
                    translations[line[0]] = line[1]
            }
        }

        operator fun get(key: String) = getOrDefault(key)

        fun getOrNull(key: String) = languages[Locale.current]?.translations?.get(key)

        fun getOrDefault(key: String, default: String = key) = getOrNull(key) ?: default
    }

    val translations = mutableMapOf<String, String>()
}