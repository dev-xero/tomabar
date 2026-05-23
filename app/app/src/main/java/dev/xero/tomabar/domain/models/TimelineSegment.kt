package dev.xero.tomabar.domain.models

enum class SessionState { Work, Rest, Idle }

data class TimelineSegment(
    val state: SessionState,
    val startMillis: Long,
    val durationMillis: Long
)