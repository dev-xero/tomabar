package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.xero.tomabar.R
import dev.xero.tomabar.domain.utils.YearlySessionStats

@Composable
fun TomaBarYearlyStatsHeader(
    yearlyStats: YearlySessionStats,
    modifier: Modifier = Modifier
) {
    Text(
        text =  stringResource(R.string.yearly_stats_header, yearlyStats.totalSessions, yearlyStats.hoursThisYear),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}