package kengine.util

import java.net.URL
import java.nio.file.Paths
import java.util.*

object Resource {
    var localPath: String = ""

    fun global(path: String, error: String = "Global resource not found at path $path") =
        Resource::class.java.getResource(path) ?: terminateError(error)

    fun local(path: String) =
        URL("file:/${Paths.get(System.getProperty("user.dir"), localPath, path)}")

    private const val UNKNOWN_STRING = "?"

    private val languages = mutableMapOf<String, Map<String, String>>()
    var locale: Locale = Locale.getDefault()
    var fallback: Locale = Locale.ENGLISH

    // TODO: Allow multiple language files for one locale
    fun addLanguage(locale: Locale, url: URL) {
        val props = Properties().apply { load(url.openStream()) }
        val lang = props.entries.associate { e -> e.key.toString() to e.value.toString() }
        languages[locale.language] = lang
        languages[locale.toString()] = lang
    }

    fun addLanguage(locale: Locale, path: String) = addLanguage(locale, local(path))

    private fun getLanguage(locale: Locale) = languages[locale.toString()] ?: languages[locale.language]

    fun getString(key: String, notFound: String = UNKNOWN_STRING): String {
        val lang = getLanguage(locale) ?: getLanguage(fallback)
        return lang?.getOrDefault(key, notFound) ?: notFound
    }
}