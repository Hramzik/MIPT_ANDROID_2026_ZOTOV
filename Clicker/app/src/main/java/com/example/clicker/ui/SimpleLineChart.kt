package com.example.clicker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class SimpleLineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private companion object {
        private const val GRAPH_LINE_WIDTH = 4f
        private const val GRID_LINE_WIDTH = 1f
        private const val TEXT_SIZE_PX = 30f
        private const val PADDING_X_PX = 60f
        private const val PADDING_Y_PX = 40f
        private const val LABLES_HEIGHT_RESERVE_PX = 40f
        private const val LABLES_Y_OFFSET_PX = 30f
        private const val GRID_LINES = 5
        private const val POINT_RADIUS = 5f
        private const val HIGHLIGHTED_POINT_RADIUS = 8f
    }

    private val colorLine = context.getColor(com.example.clicker.R.color.chart_line)
    private val colorHighlight = context.getColor(com.example.clicker.R.color.chart_highlight)
    private val colorText = context.getColor(com.example.clicker.R.color.chart_text)
    private val colorDim = context.getColor(com.example.clicker.R.color.chart_dim)

    private var values: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var highlighted: Set<Int> = emptySet()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = GRAPH_LINE_WIDTH
        style = Paint.Style.STROKE
        color = colorLine
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = colorLine
    }

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = GRID_LINE_WIDTH
        style = Paint.Style.STROKE
        color = colorDim
    }

    private val path = Path()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = TEXT_SIZE_PX
        color = colorText
    }

    fun setData(values: List<Float>, labels: List<String>, highlighted: Set<Int> = emptySet()) {
        this.values = values
        this.labels = labels
        this.highlighted = highlighted
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return

        val paddingX = PADDING_X_PX
        val paddingY = PADDING_Y_PX
        val w = width - paddingX * 2
        val h = height - paddingY * 2 - LABLES_HEIGHT_RESERVE_PX

        val max = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val stepX = if (values.size > 1) w / (values.size - 1) else w

        for (i in 0..GRID_LINES) {
            val y = paddingY + h * i / GRID_LINES
            canvas.drawLine(paddingX, y, paddingX + w, y, dimPaint)
        }

        path.rewind()
        values.forEachIndexed { idx, v ->
            val x = paddingX + idx * stepX
            val y = paddingY + h * (1f - (v / max))
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)

        values.forEachIndexed { idx, v ->
            val x = paddingX + idx * stepX
            val y = paddingY + h * (1f - (v / max))
            pointPaint.color = if (idx in highlighted) colorHighlight else colorLine
            canvas.drawCircle(x, y, if (idx in highlighted) HIGHLIGHTED_POINT_RADIUS else POINT_RADIUS, pointPaint)
        }

        for (pos in highlighted) {
            val x = paddingX + pos * stepX
            val y = paddingY + h + LABLES_Y_OFFSET_PX
            val text = labels[pos]
            val textWidth = textPaint.measureText(text)
            canvas.drawText(text, x - textWidth / 2f, y, textPaint)
        }
    }
}
