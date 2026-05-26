package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import java.time.Instant
import java.time.ZoneId

fun computeHourlyFocus(
    allHistory: List<TimelineSegment>,
    zone: ZoneId = ZoneId.systemDefault(),
): List<Int> {
    val buckets = IntArray(24)
    allHistory
        .filter { it.state == SessionState.Work }
        .forEach { seg ->
            val hour = Instant.ofEpochMilli(seg.startMillis).atZone(zone).hour
            buckets[hour] += (seg.durationMillis / 60_000).toInt()
        }
    return buckets.toList()
}