package dev.xero.tomabar.domain.models


enum class EndReason { Completed, Interrupted, Ongoing }

data class TimelineSegment(
    val state: SessionState,
    val startMillis: Long,
    val durationMillis: Long,
    val endReason: EndReason,
)