package dev.xero.tomabar.domain.utils

fun lastUpdatedLabel(millis: Long?): String {
    if (millis == null) return "Showing saved data"
    val elapsed = System.currentTimeMillis() - millis
    val minutes = elapsed / 60_000
    return when {
        minutes < 1   -> "Updated just now"
        minutes < 60  -> "Updated ${minutes}m ago"
        minutes < 1440 -> "Updated ${minutes / 60}h ago"
        else          -> "Updated ${minutes / 1440}d ago"
    }
}