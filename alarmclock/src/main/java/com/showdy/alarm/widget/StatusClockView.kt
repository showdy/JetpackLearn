package com.showdy.alarm.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Created by <b>Showdy</b> on 2020/10/23 15:43
 *
 *  状态打卡的自定义view
 */
class StatusClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleInt: Int = 0
) : View(context, attributeSet, defStyleInt) {

    private val topPath = Path()
    private val leftPath = Path()
    private val rightPath = Path()

    private val topRegion = Region()
    private val leftRegion = Region()
    private val rightRegion = Region()

    private val defaultColor: Int = 0xFF49AEF1.toInt()
    private val touchColor: Int = 0xFF4491DD.toInt()
    private val whiteColor: Int = 0xFFFFFFFF.toInt()

    private var w: Int = 0
    private var h: Int = 0

    private val texts: Triple<String, String, String> = Triple("正常", "迟缓", "异动")

    //坐标映射的matrix
    var mapMatrix: Matrix

    //按下时flag
    var downFlag = Flag.UNSET

    //当前flag
    var currentFlag = Flag.UNSET

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val touchPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    init {
        paint.color = defaultColor
        touchPaint.color = touchColor
        textPaint.color = whiteColor
        textPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            28f,
            Resources.getSystem().displayMetrics
        )
        mapMatrix = Matrix()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.w = w
        this.h = h

        mapMatrix.reset()

        //设置绘制的区域
        val region = Region(-w, -h, w, h)
        val minWidth = if (w > h) h.toFloat() else w.toFloat()
        val circle = RectF(-minWidth / 2, -minWidth / 2, minWidth / 2, minWidth / 2)
        val sweepAngle = -118f
        //30°开始，扫过118°
        topPath.moveTo(0f, -3f)
        topPath.arcTo(circle, -30f, sweepAngle, false)
        topPath.close()
        topRegion.setPath(topPath, region)

        leftPath.moveTo(-3f, 3f)
        leftPath.arcTo(circle, -150f, sweepAngle, false)
        leftPath.close()
        leftRegion.setPath(leftPath, region)

        rightPath.moveTo(3f, 3f)
        rightPath.arcTo(circle, -270f, sweepAngle, false)
        rightPath.close()

        rightRegion.setPath(rightPath, region)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val position = floatArrayOf(0f, 0f)
        position[0] = event!!.rawX
        position[1] = event!!.rawY
        mapMatrix.mapPoints(position)

        val pair = Pair(position[0].toInt(), position[1].toInt())

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downFlag = getTouchedPath(pair)
                currentFlag = downFlag
            }

            MotionEvent.ACTION_MOVE -> {
                currentFlag = getTouchedPath(pair)
            }

            MotionEvent.ACTION_UP -> {
                currentFlag = getTouchedPath(pair)
                if (currentFlag == downFlag && currentFlag != Flag.UNSET) {
                    listener?.let {
                        when (currentFlag) {
                            Flag.TOP -> it.onTopClicked(texts.first)
                            Flag.LEFT -> it.onLeftClicked(texts.third)
                            Flag.RIGHT -> it.onRightClicked(texts.second)
                            Flag.UNSET -> {
                            }
                        }
                    }
                    //重置flag
                    restFlag()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                restFlag()
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(w / 2.toFloat(), h / 2.toFloat())

        //获取测量矩阵的逆矩阵，实现坐标映射
        if (mapMatrix.isIdentity) canvas.matrix.invert(mapMatrix)

        canvas.drawPath(topPath, paint)
        canvas.drawPath(leftPath, paint)
        canvas.drawPath(rightPath, paint)

        when (currentFlag) {
            Flag.TOP -> canvas.drawPath(topPath, touchPaint)
            Flag.LEFT -> canvas.drawPath(leftPath, touchPaint)
            Flag.RIGHT -> canvas.drawPath(rightPath, touchPaint)
            else -> {
            }
        }
        drawText(canvas, texts.first, 0 to -h / 4)
        drawText(canvas, texts.third, -w / 4 to h / 8)
        drawText(canvas, texts.second, w / 4 to h / 8)
    }

    private fun getTouchedPath(pair: Pair<Int, Int>): Flag {
        return when {
            topRegion.contains(pair.first, pair.second) -> Flag.TOP
            leftRegion.contains(pair.first, pair.second) -> Flag.LEFT
            rightRegion.contains(pair.first, pair.second) -> Flag.RIGHT
            else -> Flag.UNSET
        }

    }

    enum class Flag {

        UNSET,

        TOP,

        LEFT,

        RIGHT;
    }

    private fun restFlag() {
        currentFlag = Flag.UNSET
        downFlag = Flag.UNSET
    }

    private var listener: ClockListener? = null

    fun setClockListener(listener: ClockListener?) {
        this.listener = listener
    }


    interface ClockListener {

        fun onTopClicked(text: String)

        fun onLeftClicked(text: String)

        fun onRightClicked(text: String)
    }


    private fun drawText(canvas: Canvas, text: String, center: Pair<Int, Int>) {
        //获取文字的宽度
        val width = textPaint.measureText(text)
        val x = center.first - width / 2
        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        val y = center.second + (abs(fontMetrics.ascent) - fontMetrics.descent) / 2
        canvas.drawText(text, x, y, textPaint)
    }


}

