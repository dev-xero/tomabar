package dev.xero.tomabar.domain.models

import java.time.LocalDate

data class HeatmapData(
    val year: Int,
    val activityByDate: Map<LocalDate, Int>
)