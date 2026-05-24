package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.TimelineSegment
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun List<TimelineSegment>.today(): List<TimelineSegment> {
    val zone = ZoneId.systemDefault()
    val todayDate = LocalDate.now(zone)
    return filter {
        Instant.ofEpochMilli(it.startMillis).atZone(zone).toLocalDate() == todayDate
    }
}