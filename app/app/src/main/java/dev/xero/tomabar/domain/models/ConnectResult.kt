package dev.xero.tomabar.domain.models

sealed interface ConnectResult {
    data class Success(val sessions: List<TimelineSegment>) : ConnectResult
    object Unreachable : ConnectResult
    data class ServerError(val code: Int) : ConnectResult
    object InvalidAddress : ConnectResult
}