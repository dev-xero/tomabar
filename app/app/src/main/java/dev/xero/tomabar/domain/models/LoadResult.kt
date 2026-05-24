package dev.xero.tomabar.domain.models

sealed interface LoadResult {
    data class Fresh(val sessions: List<TimelineSegment>, val fetchedAt: Long) : LoadResult
    data class Cached(val sessions: List<TimelineSegment>, val fetchedAt: Long?) : LoadResult
    object StaleConnection : LoadResult
    object NoData : LoadResult
}