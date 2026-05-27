package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.EndReason
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment

data class WorkRestBreakdown(
    val workMillis: Long,
    val restMillis: Long,
    val workPercent: Int,
)

data class CompletionBreakdown(
    val finished: Int,
    val stopped: Int,
    val completedPercent: Int,
)

fun computeWorkRest(scoped: List<TimelineSegment>): WorkRestBreakdown {
    val work = scoped.filter { it.state == SessionState.Work }.sumOf { it.durationMillis }
    val rest = scoped.filter { it.state == SessionState.Rest }.sumOf { it.durationMillis }
    val totalActive = work + rest
    val pct = if (totalActive > 0) ((work * 100) / totalActive).toInt() else 0
    return WorkRestBreakdown(work, rest, pct)
}

fun computeCompletion(allHistory: List<TimelineSegment>): CompletionBreakdown {
    val workBlocks = allHistory.filter { it.state == SessionState.Work }
    val finished = workBlocks.count { it.endReason == EndReason.Completed }
    val stopped = workBlocks.count { it.endReason == EndReason.Interrupted }
    val denom = finished + stopped
    val pct = if (denom > 0) (finished * 100) / denom else 0
    return CompletionBreakdown(finished, stopped, pct)
}
