package dev.xero.tomabar.domain.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun formatClock(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))