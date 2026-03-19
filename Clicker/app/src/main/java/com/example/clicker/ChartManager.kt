package com.example.clicker

import android.content.Context
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

    fun switchChartMode() {
        chartMode = if (chartMode == ChartMode.LAST_MINUTE) {
            ChartMode.LAST_MONTH
        } else {
            ChartMode.LAST_MINUTE
        }
        updateChart()
    }
}
