package dev.xero.tomabar.domain.models


data class TimelineSegment(
    val state: SessionState,
    val startMillis: Long,
    val durationMillis: Long
)
