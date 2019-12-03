package meewii.core

fun String?.toIntOrValue(default: Int) = try {
    this?.let {
        if (it.isBlank().not()) it.toInt() else default
    } ?: default
} catch (e: NumberFormatException) {
    default
}
