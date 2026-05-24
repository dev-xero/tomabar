package dev.xero.tomabar.domain.utils

import dev.xero.tomabar.domain.models.EndReason
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment

import org.json.JSONObject

/**
 * Parses the /metrics response: a JSON object with a "data" array of transition
 * events. Each event has fromState, toState, and a unix timestamp (seconds, with
 * fractional part). A segment's duration is the gap to the next transition.
 *
 * Dangling final state: the array can end mid-state (last event transitions INTO
 * a state that never closes). That final open interval has unknown duration, so
 * we DROP it rather than invent a length.
 */
fun parseMetrics(json: String): List<TimelineSegment> {
    val transitions = runCatching {
        val root = JSONObject(json)
        val arr = root.getJSONArray("data")
        buildList {
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                if (obj.optString("type") != "transition") continue
                add(
                    Transition(
                        event = obj.getString("event"),
                        toState = obj.getString("toState").toSessionState(),
                        timestampMillis = (obj.getDouble("timestamp") * 1000).toLong()
                    )
                )
            }
        }
    }.getOrElse { emptyList() }

    if (transitions.size < 2) return emptyList()

    return transitions.zipWithNext { current, next ->
        TimelineSegment(
            state = current.toState,
            startMillis = current.timestampMillis,
            durationMillis = next.timestampMillis - current.timestampMillis,
            endReason = when (next.event) {
                "timerFired" -> EndReason.Completed
                "startStop"  -> EndReason.Interrupted
                else          -> EndReason.Ongoing
            }
        )
    }.filter { it.durationMillis > 0 }
}

private data class Transition(val event: String, val toState: SessionState, val timestampMillis: Long)

private fun String.toSessionState(): SessionState = when (lowercase()) {
    "work" -> SessionState.Work
    "rest" -> SessionState.Rest
    else   -> SessionState.Idle
}