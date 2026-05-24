package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class StreakInfo(
    val current: Int,
    val best: Int,
    val lastActiveDate: LocalDate?,
)

fun computeStreak(
    segments: List<TimelineSegment>,
    zone: ZoneId = ZoneId.systemDefault(),
    today: LocalDate = LocalDate.now(zone),
): StreakInfo {
    val activeDays = segments
        .filter { it.state == SessionState.Work && it.durationMillis > 0 }
        .map { Instant.ofEpochMilli(it.startMillis).atZone(zone).toLocalDate() }
        .toSortedSet()

    if (activeDays.isEmpty()) return StreakInfo(0, 0, null)

    // Longest run anywhere in history.
    var best = 1
    var run = 1
    val list = activeDays.toList()
    for (i in 1 until list.size) {
        if (list[i] == list[i - 1].plusDays(1)) run++ else run = 1
        if (run > best) best = run
    }

    // Current streak: only alive if the last active day is today or yesterday.
    val lastActive = list.last()
    val current = if (lastActive < today.minusDays(1)) {
        0
    } else {
        var count = 0
        var cursor = lastActive
        while (activeDays.contains(cursor)) {
            count++
            cursor = cursor.minusDays(1)
        }
        count
    }

    return StreakInfo(current = current, best = best, lastActiveDate = lastActive)
}