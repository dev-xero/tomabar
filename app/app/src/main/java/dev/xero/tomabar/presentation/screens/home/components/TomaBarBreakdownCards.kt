package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.R
import dev.xero.tomabar.domain.models.DonutSegment
import dev.xero.tomabar.domain.utils.CompletionBreakdown
import dev.xero.tomabar.domain.utils.WorkRestBreakdown
import dev.xero.tomabar.domain.utils.formatDuration
import dev.xero.tomabar.presentation.theme.focusColors

@Composable
fun TomaBarBreakdownCards(
    workRest: WorkRestBreakdown,
    completion: CompletionBreakdown,
    modifier: Modifier = Modifier
) {
    val focus = MaterialTheme.focusColors

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.breakdown),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TomaBarBreakdownCard(title = "Work / rest") {
                DonutRing(
                    segments = listOf(
                        DonutSegment(workRest.workMillis.toFloat(), focus.work),
                        DonutSegment(workRest.restMillis.toFloat(), focus.rest)
                    ),
                    centerLabel = "${workRest.workPercent}%",
                    centerSubLabel = "work"
                )
                Spacer(Modifier.height(15.dp))
                KeyRow(focus.work, "Work", formatDuration(workRest.workMillis), showDivider = false)
                KeyRow(focus.rest, "Rest", formatDuration(workRest.restMillis), showDivider = true)
            }

            TomaBarBreakdownCard(title = "Completion") {
                DonutRing(
                    segments = listOf(
                        DonutSegment(completion.finished.toFloat(), focus.work),
                        DonutSegment(completion.stopped.toFloat(), focus.idle)
                    ),
                    centerLabel = "${completion.completedPercent}%",
                    centerSubLabel = "done"
                )
                Spacer(Modifier.height(15.dp))
                KeyRow(focus.work, "Finished", completion.finished.toString(), showDivider = false)
                KeyRow(focus.idle, "Stopped", completion.stopped.toString(), showDivider = true)
            }
        }
    }
}

@Composable
private fun RowScope.TomaBarBreakdownCard(
    title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ), shape = RoundedCornerShape(24.dp), modifier = modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun DonutRing(
    segments: List<DonutSegment>,
    centerLabel: String,
    centerSubLabel: String,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.focusColors.idle
    val strokePx = with(LocalDensity.current) { 13.dp.toPx() }
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(0.0001f)

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(116.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Butt)
            val inset = strokePx / 2f
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            var startAngle = -90f
            segments.forEach { seg ->
                val sweep = 360f * (seg.value / total)
                drawArc(
                    color = seg.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerLabel, style = MaterialTheme.typography.titleLarge)
            Text(
                centerSubLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun KeyRow(color: Color, label: String, value: String, showDivider: Boolean) {
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .size(9.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold
        )
    }
}