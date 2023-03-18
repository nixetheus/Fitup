package it.polimi.mobile.design.custom_layouts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.cardview.widget.CardView
import it.polimi.mobile.design.R
import it.polimi.mobile.design.custom_views.LineGraphDataView
import it.polimi.mobile.design.entities.DataPoint
import it.polimi.mobile.design.helpers.Constant
import java.lang.Integer.max
import java.lang.Integer.min
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit


class LineGraphLayout(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private var w = 0
    private val titleStrip = 50.toPx()
    private val fakePadding = 30.toPx()

    private var minY: Float = 0f
    private var maxY: Float = 0f
    private var minX: Long = 0
    private var maxX: Long = Long.MAX_VALUE
    private var pointViews: ArrayList<LineGraphDataView> = arrayListOf()

    var dataPoints: List<DataPoint> = listOf()

    set(points) {
        field = points

        getEdges()
    }

    init {

        setWillNotDraw(false)
        // Draw
        post{drawDataPoints()}
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(w, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isNotEmpty()) {
            drawLines(canvas)
            drawAuxiliaryLine(canvas)
            drawIntegralArea(canvas)
            drawMinMaxMid(canvas)
        }
    }

    private fun getEdges() {
        if (dataPoints.isNotEmpty()) {
            minX = dataPoints.minOf { el -> el.xcoordinate!! }
            maxX = dataPoints.maxOf { el -> el.xcoordinate!! }
            minY = dataPoints.minOf { el -> el.ycoordinate!! }
            maxY = dataPoints.maxOf { el -> el.ycoordinate!! }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun drawDataPoints() {

        cleanPoints()
        val dataPointSize = Constant.DATA_BUTTON_SIZE
        w = max((min(dataPoints.size, Constant.MAX_SIZE_GRAPH) * 100).toPx(),
            context.resources.displayMetrics.widthPixels)

        for (point in dataPoints) {

            val pointView = LineGraphDataView(context)
            pointView.dataValue = point.ycoordinate!!

            pointView.id = View.generateViewId()

            val params = LayoutParams(dataPointSize, dataPointSize)
            params.leftMargin =
                (getRelativeX(point.xcoordinate!!).toFloat() - (dataPointSize / 2f)).toInt()
            params.topMargin =
                (getRelativeY(point.ycoordinate).toFloat() - (dataPointSize / 2f)).toInt()

            pointView.setOnTouchListener(OnTouchListener { _, motionEvent ->
                return@OnTouchListener pointTouchHandler(params.leftMargin, params.topMargin,
                    point.ycoordinate, motionEvent)
            })

            addView(pointView, params)
            pointViews.add(pointView)
        }
    }

    private fun cleanPoints() {
        for (dataView in pointViews) removeView(dataView)
    }

    private fun pointTouchHandler(x: Int, y: Int, value: Float, event: MotionEvent?) : Boolean {

        if (event == null) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawValueBubble(x, y, value)
            }
        }
        return true
    }

    private fun drawValueBubble(x: Int, y: Int, value: Float) {

        val cardDim = 110
        val card = CardView(context)
        card.radius = cardDim.toPx().toFloat()
        card.background = getDrawable(context, R.drawable.gradient_arms)
        card.foregroundGravity = CENTER_VERTICAL

        val valueTextView = TextView(context)
        valueTextView.text = value.toString()
        valueTextView.textAlignment = TEXT_ALIGNMENT_CENTER
        valueTextView.typeface = Typeface.create("Lato Bold", Typeface.BOLD)
        valueTextView.setTextColor(Color.WHITE)
        valueTextView.height = cardDim
        valueTextView.gravity = Gravity.CENTER_VERTICAL
        card.addView(valueTextView)

        val bubbleParams = LayoutParams(cardDim, cardDim)
        bubbleParams.leftMargin = x
        bubbleParams.topMargin = y - cardDim
        addView(card, bubbleParams)

        val removeCardTask = Runnable { removeView(card) }
        postDelayed(removeCardTask, 2000)
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
                getRelativeX(point1.xcoordinate!!).toFloat(),
                getRelativeY(point1.ycoordinate!!).toFloat(),
                getRelativeX(point2.xcoordinate!!).toFloat(),
                getRelativeY(point2.ycoordinate!!).toFloat(),
                linesPaint
            )
        }

    }

    private fun drawAuxiliaryLine(canvas: Canvas) {

        val graphPaint = Paint()
        graphPaint.strokeWidth = 5f
        graphPaint.color = Color.argb(0.15f, 1f, 1f, 1f)
        graphPaint.style = Paint.Style.FILL

        val pointZero = dataPoints.elementAt(0)

        // XY COORDINATES
        graphPaint.color = Color.argb(1f, 1f, 1f, 1f)
        graphPaint.style = Paint.Style.STROKE
        graphPaint.strokeCap = Paint.Cap.BUTT

        canvas.drawLine(
            fakePadding.toFloat(),
            height.toFloat() - 25,
            getRelativeX(dataPoints.elementAt(dataPoints.size - 1).xcoordinate!!).toFloat(),
            height.toFloat() - 25,
            graphPaint
        )
    }

    private fun drawIntegralArea(canvas: Canvas) {

        val graphPaint = Paint()
        graphPaint.strokeWidth = 7.5f
        graphPaint.style = Paint.Style.FILL
        graphPaint.color = Color.argb(0.15f, 1f, 1f, 1f)

        // POLYGON
        val graphPath = Path()
        val pointZero = dataPoints.elementAt(0)
        graphPath.moveTo(
            getRelativeX(pointZero.xcoordinate!!).toFloat(),
            getRelativeY(pointZero.ycoordinate!!).toFloat())

        for (pointIndex in 1 until dataPoints.size) {

            val point = dataPoints.elementAt(pointIndex)
            graphPath.lineTo(
                getRelativeX(point.xcoordinate!!).toFloat(),
                getRelativeY(point.ycoordinate!!).toFloat())
        }

        // Last Three Points
        graphPath.lineTo(
            getRelativeX(dataPoints.elementAt(dataPoints.size - 1).xcoordinate!!).toFloat(),
            height.toFloat() - 25)

        graphPath.lineTo(
            getRelativeX(pointZero.xcoordinate).toFloat(),
            height.toFloat() - 25)

        graphPath.lineTo(
            getRelativeX(pointZero.xcoordinate).toFloat(),
            getRelativeY(pointZero.ycoordinate).toFloat())

        canvas.drawPath(graphPath, graphPaint)
    }

    private fun drawMinMaxMid(canvas: Canvas) {

        val graphPaint = Paint()
        graphPaint.strokeWidth = 5f
        graphPaint.style = Paint.Style.FILL
        graphPaint.color = Color.argb(0.5f, 1f, 1f, 1f)
        graphPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)

        val pointZero = dataPoints.elementAt(0)
        val pointEnd = dataPoints.elementAt(dataPoints.size - 1)

        for (y in listOf(minY, (minY + maxY) / 2, maxY)) {
            canvas.drawLine(
                getRelativeX(pointZero.xcoordinate!!).toFloat() - 50,
                getRelativeY(y).toFloat(),
                getRelativeX(pointEnd.xcoordinate!!).toFloat() + 50,
                getRelativeY(y).toFloat(),
                graphPaint
            )
        }
    }

    private fun getRelativeX(dimensionX: Long): Int {
        val percentage = if (maxX == minX) 0.5f
        else ((dimensionX - minX).toFloat() / (maxX - minX).toFloat())
        return (percentage * (w - 2 * fakePadding) + fakePadding).toInt()
    }

    private fun getRelativeY(dimensionY: Float): Int {

        val availableHeight = (height - 2 * fakePadding) - titleStrip
        val percentage = if (maxY == minY) 0.5f else ((dimensionY - minY) / (maxY - minY))
        return (availableHeight - (percentage * availableHeight) + fakePadding).toInt() +
                titleStrip
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}