package dev.xero.tomabar.domain.utils

fun formatDuration(millis: Long): String {
    val totalMinutes = millis / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "%dh %02dm".format(hours, minutes)
        else      -> "%dm".format(minutes)
    }
}