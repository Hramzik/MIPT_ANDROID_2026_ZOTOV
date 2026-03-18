package com.example.clicker

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate

class ChartManager(
    private val clicksChart: LineChart,
    private val clickCounter: ClickCounter
) {
    enum class ChartMode {
        LAST_MONTH, LAST_MINUTE
    }

    private var chartMode: ChartMode = ChartMode.LAST_MINUTE

    init {
        configureChart()
    }

    private fun configureChart() {
        clicksChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)

            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }
        }
    }

    fun updateChart() {
        val dataSet = collectDataSet()
        
        configureDataSet(dataSet)
        showDataSet(dataSet)
    }

    private fun showDataSet(dataSet: LineDataSet) {
        val data = LineData(dataSet)
        clicksChart.data = data
        clicksChart.invalidate()
    }

    private fun collectDataSet(): LineDataSet {
        val entries: List<Entry>
        val title: String

        when (chartMode) {
            ChartMode.LAST_MONTH -> {
                val dailyClicks = clickCounter.getDailyClicks()
                val sortedDates = dailyClicks.keys.sorted()
                entries = sortedDates.mapIndexed { index, date ->
                    Entry(index.toFloat(), dailyClicks[date]?.toFloat() ?: 0f)
                }
                title = "Daily Clicks (Last 30 Days)"
            }
            ChartMode.LAST_MINUTE -> {
                val intervalClicks = clickCounter.getLastMinuteClicksByInterval()
                entries = intervalClicks.mapIndexed { index, clicks ->
                    Entry((index * 10).toFloat(), clicks.toFloat())
                }
                title = "Clicks (Last Minute, 10-sec intervals)"
            }
        }

        return LineDataSet(entries, title)
    }

    private fun configureDataSet(dataSet: LineDataSet) {
        dataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(ColorTemplate.COLORFUL_COLORS[0])
        dataSet.circleHoleColor = ColorTemplate.COLORFUL_COLORS[0]
        dataSet.mode = LineDataSet.Mode.LINEAR
        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = ColorTemplate.COLORFUL_COLORS[0]
    }

    fun switchChartMode(mode: ChartMode) {
        chartMode = mode
        updateChart()
    }
}
