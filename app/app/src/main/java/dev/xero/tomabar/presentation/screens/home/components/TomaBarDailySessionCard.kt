package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.R
import dev.xero.tomabar.domain.models.SessionState
import dev.xero.tomabar.domain.models.TimelineSegment
import dev.xero.tomabar.domain.utils.formatClock
import dev.xero.tomabar.presentation.theme.focusColors

@Composable
fun TomaBarDailySessionCard(
    segments: List<TimelineSegment>, modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            stringResource(R.string.today_s_session),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TomaBarSessionCard(segments = segments)
    }
}

@Composable
private fun TomaBarSessionCard(
    segments: List<TimelineSegment>, modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ), shape = RoundedCornerShape(24.dp), modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            TomaBarSessionHeader()
            Spacer(Modifier.height(16.dp))

            TomaBarSessionTimeline(segments = segments)
            Spacer(Modifier.height(9.dp))

            TimelineAxis(segments)
            Spacer(Modifier.height(15.dp))

            TimelineLegend()        }
    }
}

@Composable
fun TomaBarSessionHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.timeline), style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    "Today",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp, 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TomaBarSessionTimeline(
    segments: List<TimelineSegment>,
    modifier: Modifier = Modifier
) {
    val focus = MaterialTheme.focusColors
    val density = LocalDensity.current
    val gapPx = with(density) { 2.dp.toPx() }
    val cornerPx = with(density) { 12.dp.toPx() }

    val total = segments.sumOf { it.durationMillis }.coerceAtLeast(1L)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val totalGaps = gapPx * (segments.size - 1).coerceAtLeast(0)
        val drawableWidth = size.width - totalGaps
        val lastIndex = segments.lastIndex

        var x = 0f
        segments.forEachIndexed { index, segment ->
            val w = drawableWidth * (segment.durationMillis.toFloat() / total)
            val color = when (segment.state) {
                SessionState.Work -> focus.work
                SessionState.Rest -> focus.rest
                SessionState.Idle -> focus.idle
            }

            // round only the outer corners of the whole track
            val left = if (index == 0) cornerPx else 0f
            val right = if (index == lastIndex) cornerPx else 0f

            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset(x, 0f), Size(w, size.height)),
                        topLeft = CornerRadius(left, left),
                        bottomLeft = CornerRadius(left, left),
                        topRight = CornerRadius(right, right),
                        bottomRight = CornerRadius(right, right)
                    )
                )
            }
            drawPath(path, color)

            x += w + gapPx
        }
    }
}

@Composable
private fun TimelineAxis(
    segments: List<TimelineSegment>,
    modifier: Modifier = Modifier
) {
    if (segments.isEmpty()) return

    val startMillis = segments.first().startMillis
    val endMillis = segments.last().let { it.startMillis + it.durationMillis }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            formatClock(startMillis),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            formatClock(endMillis),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun TimelineLegend(modifier: Modifier = Modifier) {
    val focus = MaterialTheme.focusColors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LegendDot(focus.work, "Work")
        LegendDot(focus.rest, "Rest")
        LegendDot(focus.idle, "Idle")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(
            Modifier
                .size(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}