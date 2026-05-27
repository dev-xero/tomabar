package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.R
import dev.xero.tomabar.domain.models.HeatmapData
import dev.xero.tomabar.presentation.theme.focusColors
import java.time.LocalDate

@Composable
fun TomaBarYearlyHeatmap(heatmapData: HeatmapData, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                TomaBarYearlyHeatmapHeader()
                Spacer(Modifier.height(16.dp))
                HeatmapGrid(data = heatmapData)
                Spacer(Modifier.height(14.dp))
                HeatmapLegend()
            }
        }
    }
}

@Composable
private fun TomaBarYearlyHeatmapHeader(modifier: Modifier = Modifier) {
    val current = LocalDate.now()
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.yearly_activity),
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = current.year.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp, 6.dp)
                )
            }
        }
    }
}
@Composable
private fun HeatmapGrid(
    data: HeatmapData,
    modifier: Modifier = Modifier
) {
    val focus = MaterialTheme.focusColors
    val emptyColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val maxCount = remember(data) {
        data.activityByDate.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    }

    val months = remember(data.year) { buildHeatmapMonths(data.year) }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        months.forEach { month ->
            MonthBlock(
                month = month,
                data = data,
                maxCount = maxCount,
                emptyColor = emptyColor,
                workColor = focus.work
            )
        }
    }
}

@Composable
private fun MonthBlock(
    month: HeatmapMonth,
    data: HeatmapData,
    maxCount: Int,
    emptyColor: Color,
    workColor: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .height((7 * 12).dp + (6 * 3).dp)
                .width((month.weekCount * 12).dp + ((month.weekCount - 1) * 3).dp)
        ) {
            items(month.days) { day ->
                val color = when {
                    day == null -> Color.Transparent
                    else -> {
                        val count = data.activityByDate[day] ?: 0
                        if (count == 0) emptyColor
                        else workColor.copy(alpha = levelAlpha(count, maxCount))
                    }
                }
                Box(
                    Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(color)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            month.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class HeatmapMonth(
    val label: String,
    val days: List<LocalDate?>,
    val weekCount: Int,
)

private fun buildHeatmapMonths(year: Int): List<HeatmapMonth> {
    return (1..12).map { m ->
        val first = LocalDate.of(year, m, 1)
        val length = first.lengthOfMonth()
        val leadingBlanks = first.dayOfWeek.value - 1

        val days = buildList {
            repeat(leadingBlanks) { add(null) }
            for (d in 1..length) add(LocalDate.of(year, m, d))
            while (size % 7 != 0) add(null)
        }
        HeatmapMonth(
            label = first.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()),
            days = days,
            weekCount = days.size / 7
        )
    }
}

@Composable
private fun HeatmapLegend(modifier: Modifier = Modifier) {
    val focus = MaterialTheme.focusColors
    val empty = MaterialTheme.colorScheme.surfaceContainerHighest
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "312 active days", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Less", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            listOf(
                empty, focus.work.copy(alpha = 0.3f), focus.work.copy(alpha = 0.52f),
                focus.work.copy(alpha = 0.74f), focus.work
            ).forEach {
                Box(Modifier
                    .size(11.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(it))
            }
            Text(
                "More", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun levelAlpha(count: Int, max: Int): Float {
    val ratio = count.toFloat() / max
    return when {
        ratio <= 0.25f -> 0.3f
        ratio <= 0.5f -> 0.52f
        ratio <= 0.75f -> 0.74f
        else -> 1f
    }
}