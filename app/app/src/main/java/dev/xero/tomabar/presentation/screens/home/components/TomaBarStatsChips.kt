package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.domain.utils.SessionStats

@Composable
fun TomaBarStatsChips(
    stats: SessionStats,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        StatChip(
            stat = stats.workRestRatio?.let { "%.1f×".format(it) } ?: "—",
            label = "Work : rest"
        )
        StatChip(stat = "${stats.completedPercent}%", label = "Completed")
        StatChip(
            stat = stats.peakHour?.let { formatHour(it) } ?: "—",
            label = "Peak hour"
        )
    }
}

@Composable
private fun RowScope.StatChip(modifier: Modifier = Modifier, stat: String, label: String) {
    Column(
        modifier = modifier
            .weight(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Text(stat, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatHour(hour: Int): String {
    val period = if (hour < 12) "am" else "pm"
    val h = when { hour == 0 -> 12; hour > 12 -> hour - 12; else -> hour }
    return "$h$period"
}