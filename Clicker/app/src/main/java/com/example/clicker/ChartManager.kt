package com.example.clicker

import android.content.Context
import android.content.SharedPreferences
import com.example.clicker.utils.colorFromAttr
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ChartManager(
    private val clicksChart: LineChart,
    private val clickCounter: ClickCounter,
    private val context: Context,
    ) {
    enum class ChartMode {
        LAST_MONTH, LAST_MINUTE
    }

    companion object {
        private const val PREFS_NAME = "chart_prefs"
        private const val KEY_CHART_MODE = "chart_mode"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var chartMode: ChartMode = ChartMode.LAST_MONTH
    private var lastMonthDateLabels: List<String> = emptyList()
    private val dateManager: DateManager = DateManager()

    private inner class SecondsAgoFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val suffix = context.getString(R.string.click_chart_seconds_ago_suffix)
            return "${45 - value.toInt()}$suffix"
        }
    }

    private inner class ClicksFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return if (value == 0f) "" else value.toInt().toString()
        }
    }

    private inner class DateFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < lastMonthDateLabels.size) {
                lastMonthDateLabels[index]
            } else {
                ""
            }
        }
    }

    init {
        loadSettings()
        configureChart()
    }

    private fun configureChart() {
        val axisTextColor = context.colorFromAttr(com.google.android.material.R.attr.colorOnSurface)

        clicksChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDragEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)

            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
                granularity = 1f
                valueFormatter = ClicksFormatter()
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setLabelCount(4, true)
                setAvoidFirstLastClipping(true)
            }
            axisTextColor?.let {
                axisLeft.textColor = it
                xAxis.textColor = it
            }
        }
    }

    fun updateChart() {
        val dataSet = collectDataSet()

        configureDataSet(dataSet)

        when (chartMode) {
            ChartMode.LAST_MINUTE -> {
                clicksChart.xAxis.valueFormatter = SecondsAgoFormatter()
            }
            ChartMode.LAST_MONTH -> {
                clicksChart.xAxis.valueFormatter = DateFormatter()
            }
        }

        showDataSet(dataSet)
    }

    private fun showDataSet(dataSet: LineDataSet) {
        val data = LineData(dataSet)
        clicksChart.data = data
        clicksChart.invalidate()
    }

    private fun collectDataSet(): LineDataSet {
        val entries: List<Entry>

        when (chartMode) {
            ChartMode.LAST_MONTH -> {
                lastMonthDateLabels = dateManager.getLastNDayLabels(30)

                entries = clickCounter.getLastNDaylyClicks(30).mapIndexed { index, clickCount ->
                    Entry(index.toFloat(), clickCount.toFloat())
                }
            }
            ChartMode.LAST_MINUTE -> {
                entries = clickCounter.getLastMinuteClicksByInterval().mapIndexed { index, clickCount ->
                    Entry((index * 15).toFloat(), clickCount.toFloat())
                }
            }
        }

        return LineDataSet(entries, "")
    }

    private fun configureDataSet(dataSet: LineDataSet) {
        val baseColor = ColorTemplate.COLORFUL_COLORS[0]
        val highlightedPointColor = ColorTemplate.COLORFUL_COLORS[3]

        dataSet.color = baseColor
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(baseColor)
        dataSet.mode = LineDataSet.Mode.LINEAR
        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = baseColor

        if (chartMode == ChartMode.LAST_MONTH) {
            val highlightedIndices = calculateMonthHighlightIndices(dataSet.entryCount)
            dataSet.circleColors = MutableList(dataSet.entryCount) { index ->
                if (index in highlightedIndices) highlightedPointColor else baseColor
            }
        }
    }

    private fun calculateMonthHighlightIndices(pointCount: Int, labelCount: Int = 4): Set<Int> {
        val minX = 0f
        val maxX = (pointCount - 1).toFloat()
        val interval = (maxX - minX) / (labelCount - 1)

        return (0 until labelCount).mapTo(mutableSetOf()) { index ->
            (minX + index * interval).toInt()
        }
    }

    private fun loadSettings() {
        val storedModeName = sharedPreferences.getString(KEY_CHART_MODE, ChartMode.LAST_MONTH.name)
        ChartMode.entries.firstOrNull { it.name == storedModeName }?.let {
            chartMode = it
        }
    }

    private fun saveSettings() {
        sharedPreferences.edit().putString(KEY_CHART_MODE, chartMode.name).apply()
    }

    fun switchChartMode() {
        chartMode = if (chartMode == ChartMode.LAST_MINUTE) {
            ChartMode.LAST_MONTH
        } else {
            ChartMode.LAST_MINUTE
        }

        saveSettings()
        updateChart()
    }
}
