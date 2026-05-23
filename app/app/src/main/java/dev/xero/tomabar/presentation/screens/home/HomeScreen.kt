package dev.xero.tomabar.presentation.screens.home

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import dev.xero.tomabar.presentation.screens.home.components.TomaBarAppBar
import dev.xero.tomabar.presentation.screens.home.components.TomaBarBreakdownCards
import dev.xero.tomabar.presentation.screens.home.components.TomaBarDailySessionCard
import dev.xero.tomabar.presentation.screens.home.components.TomaBarSessionHistograms
import dev.xero.tomabar.presentation.screens.home.components.TomaBarStatsChips
import dev.xero.tomabar.presentation.screens.home.components.TomaBarStreakCard
import java.time.LocalDate
import java.time.ZoneId

private val sampleSegments: List<TimelineSegment> = run {
    val start = LocalDate.now()
        .atTime(9, 42)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val durations = listOf(
        SessionState.Work to 25 * 60_000L,
        SessionState.Rest to 5 * 60_000L,
        SessionState.Work to 25 * 60_000L,
        SessionState.Rest to 5 * 60_000L,
        SessionState.Idle to 4 * 60_000L,
        SessionState.Work to 22 * 60_000L,
    )

    var cursor = start
    durations.map { (state, dur) ->
        TimelineSegment(state, startMillis = cursor, durationMillis = dur).also {
            cursor += dur
        }
    }
}
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TomaBarAppBar() },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.padding(12.dp)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding,
        ) {
            item {
                TomaBarStreakCard()
                TomaBarStatsChips()
                TomaBarDailySessionCard(sampleSegments)
                TomaBarBreakdownCards()
                TomaBarSessionHistograms()
            }
        }
    }
}
