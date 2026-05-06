package com.example.clicker

import android.content.Context
import com.example.clicker.ui.SimpleLineChart

class ChartManager(
    private val clicksChart: SimpleLineChart,
    private val clickCounter: ClickCounter,
    private val context: Context,
) {
    private companion object {
        private const val DEFAULT_LABEL_COUNT = 4
    }
    private var lastMonthDateLabels: List<String> = emptyList()
    private val dateManager: DateManager = DateManager()

    init {
        updateChart()
    }

    fun updateChart() {
        val (values, labels, highlighted) = collectMonthData()
        clicksChart.setData(values, labels, highlighted)
    }

    private fun collectMonthData(): Triple<List<Float>, List<String>, Set<Int>> {
        val rawLabels = dateManager.getLastNDayLabels(30)
        val todayLabel = context.getString(R.string.click_chart_today)
        lastMonthDateLabels = rawLabels.mapIndexed { index, label ->
            if (index == rawLabels.lastIndex) todayLabel else label
        }

        val values = clickCounter.getLastNDaylyClicks(30).map { it.toFloat() }
        val highlighted = calculateMonthHighlightIndices(values.size)

        return Triple(values, lastMonthDateLabels, highlighted)
    }

    private fun calculateMonthHighlightIndices(pointCount: Int, labelCount: Int = DEFAULT_LABEL_COUNT): Set<Int> {
        val minX = 0f
        val maxX = (pointCount - 1).toFloat()
        val interval = (maxX - minX) / (labelCount - 1)

        return (0 until labelCount).mapTo(mutableSetOf()) { index ->
            (minX + index * interval).toInt()
        }
    }
}
