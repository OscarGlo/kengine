package kengine.util

import kotlin.math.pow
import kotlin.math.round

actual fun String.format(vararg values: Any?): String {
    var i = 0

    return this.replace(Regex("%(\\W*)(\\d*)((?:.\\d+)?)([bcdfs])")) { match ->
        val value = values[i++]
        val (flags, width, precision, type) = match.destructured

        val prec = precision.substring(1).toIntOrNull()

        var formatted = when (type) {
            "b" -> if (value is Boolean) value else value != null
            "c" -> value as Char
            "d" -> (value as Number).toInt()
            "f" -> if (prec != null) 10f.pow(prec).let { round(value as Float * it) / it }
            else value as Float
            "s" -> if (prec != null) value.toString().substring(0, prec) else value
            else -> ""
        }.toString()

        if (width.isNotEmpty()) {
            val pad = if (flags.contains('-')) String::padStart else String::padEnd
            formatted = pad(formatted, width.toInt() - formatted.length, ' ')
        }

        formatted
    }
}