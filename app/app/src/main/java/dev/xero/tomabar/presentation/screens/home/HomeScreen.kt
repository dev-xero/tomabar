package dev.xero.tomabar.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.xero.tomabar.R
import dev.xero.tomabar.domain.models.HeatmapData
import dev.xero.tomabar.domain.models.TimelineSegment
import dev.xero.tomabar.domain.utils.computeSessionStats
import dev.xero.tomabar.domain.utils.computeStreak
import dev.xero.tomabar.domain.utils.today
import dev.xero.tomabar.presentation.screens.home.components.OfflineBanner
import dev.xero.tomabar.presentation.screens.home.components.TomaBarAppBar
import dev.xero.tomabar.presentation.screens.home.components.TomaBarBreakdownCards
import dev.xero.tomabar.presentation.screens.home.components.TomaBarDailyTimelineCard
import dev.xero.tomabar.presentation.screens.home.components.TomaBarSessionHistograms
import dev.xero.tomabar.presentation.screens.home.components.TomaBarStatsChips
import dev.xero.tomabar.presentation.screens.home.components.TomaBarStreakCard
import dev.xero.tomabar.presentation.screens.home.components.TomaBarYearlyHeatmap
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNeedsReconnect: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeVMFactory.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is HomeUiState.NeedsReconnect) onNeedsReconnect()
    }

    Scaffold(
        topBar = { TomaBarAppBar() },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.padding(12.dp)
    ) { innerPadding ->
        when (val s = state) {
            is HomeUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Empty -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_yet),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is HomeUiState.Ready -> {
                HomeContent(
                    sessions = s.sessions,
                    isOffline = s.isOffline,
                    lastUpdated = s.lastUpdated,
                    contentPadding = innerPadding,
                )
            }

            is HomeUiState.NeedsReconnect -> Unit
        }
    }
}

@Composable
private fun HomeContent(
    sessions: List<TimelineSegment>,
    isOffline: Boolean,
    lastUpdated: Long?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.consumeWindowInsets(contentPadding),
        contentPadding = contentPadding,
    ) {
        if (isOffline) {
            item {
                OfflineBanner(lastUpdated = lastUpdated)
            }
        }
        item {
            TomaBarStreakCard(computeStreak(sessions))
            TomaBarStatsChips(
                computeSessionStats(
                    scoped = sessions.today(),
                    allHistory = sessions
                )
            )
            TomaBarDailyTimelineCard(sessions.today())
            TomaBarBreakdownCards()
            TomaBarSessionHistograms()
            TomaBarYearlyHeatmap(sampleHeatmapData)
        }
    }
}

private val sampleHeatmapData: HeatmapData = run {
    val year = LocalDate.now().year
    val random = java.util.Random(42)

    val map = mutableMapOf<LocalDate, Int>()
    var day = LocalDate.of(year, 1, 1)
    val today = LocalDate.now()

    while (!day.isAfter(today)) {
        val isWeekend = day.dayOfWeek.value >= 6
        val roll = random.nextDouble()

        val count = when {
            roll < (if (isWeekend) 0.55 else 0.25) -> 0
            roll < 0.70 -> 1 + random.nextInt(2)
            roll < 0.90 -> 3 + random.nextInt(3)
            else -> 6 + random.nextInt(4)
        }
        if (count > 0) map[day] = count
        day = day.plusDays(1)
    }

    HeatmapData(year = year, activityByDate = map)
}