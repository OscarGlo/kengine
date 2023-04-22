package kengine.util

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.files.Blob
import kotlin.js.Promise

actual class Resource actual constructor(actual val path: String, local: Boolean) {
    private var response: Response? = null
    private var text: String? = null
    private var blob: Blob? = null

    private var promise: Promise<*>?

    init {
        val params = object : RequestInit {}

        promise = window.fetch(path, params)
            .then { res ->
                response = res
                promise = null
            }
    }

    actual suspend fun waitForLoad() {
        while (promise != null)
            promise!!.await()
    }

    actual suspend fun getText(): String {
        waitForLoad()
        if (text == null)
            text = response?.text()?.await()
        return text ?: ""
    }

    suspend fun getBlob(): Blob {
        waitForLoad()
        if (blob == null)
            blob = response?.blob()?.await()
        return blob ?: Blob()
    }
}