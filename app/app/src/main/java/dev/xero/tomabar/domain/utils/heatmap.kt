package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.HeatmapData
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun computeHeatmap(
    allHistory: List<TimelineSegment>,
    year: Int = LocalDate.now().year,
    zone: ZoneId = ZoneId.systemDefault(),
): HeatmapData {
    val byDate = allHistory
        .filter { it.state == SessionState.Work && it.durationMillis > 0 }
        .map { Instant.ofEpochMilli(it.startMillis).atZone(zone).toLocalDate() }
        .filter { it.year == year }
        .groupingBy { it }
        .eachCount()
    return HeatmapData(year = year, activityByDate = byDate)
}