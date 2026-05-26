package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.R

@Composable
fun TomaBarSessionHistograms(
    hourlyFocus: List<Int>,
    modifier: Modifier = Modifier
) {
    val peakHour = remember(hourlyFocus) {
        hourlyFocus.indices.maxByOrNull { hourlyFocus[it] } ?: 0
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.patterns),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                TomaBarSessionHistogramCardHeader(peakHour = peakHour)
                Spacer(Modifier.height(20.dp))
                TomaBarFocusHistogram(
                    hourlyFocus = hourlyFocus,
                    peakHour = peakHour
                )
            }
        }
    }
}

@Composable
private fun TomaBarSessionHistogramCardHeader(
    peakHour: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.focus_by_hour),
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.peak, "%02d:00".format(peakHour)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp, 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TomaBarFocusHistogram(
    hourlyFocus: List<Int>,
    peakHour: Int,
    modifier: Modifier = Modifier
) {
    val maxValue = (hourlyFocus.maxOrNull() ?: 0).coerceAtLeast(1)
    val mutedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)
    val peakColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            hourlyFocus.forEachIndexed { hour, value ->
                val target = if (value == 0) 0f
                else (value.toFloat() / maxValue).coerceIn(0.04f, 1f)
                val fraction by animateFloatAsState(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                    label = "bar_$hour"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (hour == peakHour) peakColor else mutedColor)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("00", "06", "12", "18", "23").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val sampleHourlyFocus = listOf(
    0, 0, 0, 0, 0, 2, 8, 18, 34, 52, 68, 49,
    40, 30, 44, 38, 26, 20, 22, 15, 10, 6, 2, 0
)