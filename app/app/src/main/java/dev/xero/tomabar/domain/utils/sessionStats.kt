package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.EndReason
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import java.time.Instant
import java.time.ZoneId

data class SessionStats(
    val workRestRatio: Float?,
    val completedPercent: Int,
    val peakHour: Int?,
)

fun computeSessionStats(
    scoped: List<TimelineSegment>,
    allHistory: List<TimelineSegment>,
    zone: ZoneId = ZoneId.systemDefault(),
): SessionStats {
    // Work : rest ratio
    val workMs = scoped.filter { it.state == SessionState.Work }.sumOf { it.durationMillis }
    val restMs = scoped.filter { it.state == SessionState.Rest }.sumOf { it.durationMillis }
    val ratio = if (restMs > 0) workMs.toFloat() / restMs else null

    // Completed
    val workBlocks = scoped.filter { it.state == SessionState.Work }
    val completed = workBlocks.count { it.endReason == EndReason.Completed }
    val pct = if (workBlocks.isNotEmpty()) (completed * 100) / workBlocks.size else 0

    // Peak hour
    val byHour = allHistory
        .filter { it.state == SessionState.Work }
        .groupBy { Instant.ofEpochMilli(it.startMillis).atZone(zone).hour }
        .mapValues { (_, segs) -> segs.sumOf { it.durationMillis } }
    val peak = byHour.maxByOrNull { it.value }?.key

    return SessionStats(ratio, pct, peak)
}