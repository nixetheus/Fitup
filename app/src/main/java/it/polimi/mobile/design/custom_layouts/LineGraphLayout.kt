package it.polimi.mobile.design.custom_layouts

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import it.polimi.mobile.design.custom_objects.DataPoint
import it.polimi.mobile.design.custom_views.LineGraphDataView
import it.polimi.mobile.design.helpers.Constant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit


class LineGraphLayout(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val titleStrip = 50.toPx()
    private val fakePadding = 40.toPx()

    private var minY: Float = 0f
    private var maxY: Float = 0f
    private var minX: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.MIN)
    private var maxX: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.MIN)

    private var dataPoints: List<DataPoint> = listOf()
    set(points) {
        field = points

        getEdges()
        //drawDataPoints()
    }

    init {

        setWillNotDraw(false)

        // TODO: testing
        dataPoints = listOf(DataPoint(LocalDateTime.of(2022, 5, 6, 12, 0), 60.3f),
            DataPoint(LocalDateTime.of(2022, 7, 7, 12, 0), 57.4f),
            DataPoint(LocalDateTime.of(2022, 7, 8, 12, 0), 52.1f),
            DataPoint(LocalDateTime.of(2022, 8, 9, 12, 0), 55.9f),
            DataPoint(LocalDateTime.of(2022, 12, 10, 12, 0), 58.6f))

        // Draw
        post{drawDataPoints()}
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension((dataPoints.size * 100).toPx(), heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLines(canvas)
        drawGrid(canvas)
    }

    private fun getEdges() {
        if (dataPoints.isNotEmpty()) {
            minX = dataPoints.minOf { el -> el.xCoordinate }
            maxX = dataPoints.maxOf { el -> el.xCoordinate }
            minY = dataPoints.minOf { el -> el.yCoordinate }
            maxY = dataPoints.maxOf { el -> el.yCoordinate }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawDataPoints() {

        val dataPointSize = Constant.DATA_BUTTON_SIZE
        for (point in dataPoints) {

            val pointView = LineGraphDataView(context)
            pointView.dataValue = point.yCoordinate

            pointView.id = View.generateViewId()

            val params = LayoutParams(dataPointSize, dataPointSize)
            params.leftMargin =
                (getRelativeX(point.xCoordinate).toFloat() - (dataPointSize / 2f)).toInt()
            params.topMargin =
                (getRelativeY(point.yCoordinate).toFloat() - (dataPointSize / 2f)).toInt()

            pointView.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
                return@OnTouchListener pointTouchHandler(params.leftMargin, params.topMargin,
                    motionEvent)
            })

            addView(pointView, params)
        }
    }

    private fun pointTouchHandler(x: Int, y: Int, event: MotionEvent?) : Boolean {

        if (event == null) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawValueBubble(x, y)
            }
        }
        return true
    }

    private fun drawValueBubble(x: Int, y: Int) {

        val card = CardView(context)

        Toast.makeText(context, x.toString(), Toast.LENGTH_SHORT).show()
        val bubbleParams = LayoutParams(75, 75)
        bubbleParams.leftMargin = x
        bubbleParams.topMargin = y
        addView(card, bubbleParams)
    }

    private fun drawLines(canvas: Canvas) {

        val linesPaint = Paint()
        linesPaint.strokeWidth = 7.5f
        linesPaint.color = Color.WHITE
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND

        for (pointIndex in 0 until dataPoints.size - 1) {

            val point1 = dataPoints.elementAt(pointIndex)
            val point2 = dataPoints.elementAt(pointIndex + 1)

            canvas.drawLine(
                getRelativeX(point1.xCoordinate).toFloat(),
                getRelativeY(point1.yCoordinate).toFloat(),
                getRelativeX(point2.xCoordinate).toFloat(),
                getRelativeY(point2.yCoordinate).toFloat(),
                linesPaint
            )
        }

    }

    private fun drawGrid(canvas: Canvas) {

        val n = 4
        val linesPaint = Paint()
        linesPaint.strokeWidth = 1f
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND

        val positions = floatArrayOf(0.05f, 0.5f, 0.95f)
        val fadedWhite = Color.parseColor("#AAFFFFFF")
        val transparentWhite = Color.parseColor("#11FFFFFF")
        val colors = intArrayOf(transparentWhite, fadedWhite, transparentWhite)

        for (index in 1 until n) {

            // X Lines
            linesPaint.shader = LinearGradient(0f, index * (height.toFloat() / n),
                width.toFloat(), index * (height.toFloat() / n), colors, positions,
                Shader.TileMode.REPEAT)
            canvas.drawLine(
                0f, index * ((height - titleStrip).toFloat() / n) + titleStrip,
                width.toFloat(), index * ((height - titleStrip).toFloat() / n) + titleStrip,
                linesPaint
            )

            // Y Lines
            linesPaint.shader = LinearGradient(index * (width.toFloat() / n), 0f,
                index * (width.toFloat() / n), height.toFloat(), colors, positions,
                Shader.TileMode.CLAMP)
            canvas.drawLine(
                index * (width.toFloat() / n), titleStrip.toFloat(),
                index * (width.toFloat() / n), height.toFloat(), linesPaint
            )
        }
    }

    private fun getRelativeX(dimensionX: LocalDateTime): Int {
        val percentage = if (maxX == minX) 0.5f else
            ChronoUnit.DAYS.between(minX, dimensionX).toFloat() / ChronoUnit.DAYS.between(minX, maxX)
        return (percentage * (width - 2 * fakePadding) + fakePadding).toInt()
    }

    private fun getRelativeY(dimensionY: Float): Int {

        val availableHeight = (height - 2 * fakePadding) - titleStrip
        val percentage = if (maxY == minY) 0.5f else (dimensionY - minY) / (maxY - minY)
        return (availableHeight - (percentage * availableHeight) + fakePadding).toInt() +
                titleStrip
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}