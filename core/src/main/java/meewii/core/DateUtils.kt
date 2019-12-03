package meewii.core

import java.text.SimpleDateFormat
import java.util.*

fun Long.formatDateTo(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return try {
        sdf.format(this)
    } catch (e: IllegalArgumentException) {
        ""
    }
}