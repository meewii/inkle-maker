@file:Suppress("PrivatePropertyName", "PropertyName")

package com.gdt.inklemaker.ui.patterns

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

@SuppressLint("ViewConstructor")
abstract class BasePatternView @JvmOverloads constructor(
    colorMap: Map<String, Int>, // Map<Yarn Id, Color-Argb>
    res: PatternViewResHolder,
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // The ratio according to which the view is zoomed in or out
    abstract var scale: Float
    // Number of times we will draw the 2 rows (upper and lower threads rows)
    abstract val doubleRowCount: Int
    // Width of the drawing once it is created (including paddings and scale)
    abstract val totalWidth: Float
    // Height of the drawing once it is created (including paddings and scale)
    abstract val totalHeight: Float

    protected var cellBorderSize = res.CELL_BORDER_SIZE * 1f
    protected var cellBorderSelectedSize = res.CELL_BORDER_SELECTED_SIZE * 1f

    protected val paintMap: Map<String, Paint> = colorMap.map { entry ->
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = entry.value
        }
        Pair(entry.key, paint)
    }.toMap()

    // fun part
    override fun onDraw(canvas: Canvas) {
        for (i in 0 until doubleRowCount) {
            drawUpperLine(canvas, i)
            drawLowerLine(canvas, i)
        }
    }

    abstract fun drawUpperLine(canvas: Canvas, row: Int)
    abstract fun drawLowerLine(canvas: Canvas, row: Int)

    // Called to determine the size requirements for this view and all of its children.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize  // Must be this size
            MeasureSpec.AT_MOST -> totalWidth.toInt().coerceAtMost(widthSize) // Can't be bigger than...
            else -> totalWidth.toInt() // Be whatever you want
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> totalHeight.toInt().coerceAtMost(heightSize)
            else -> totalHeight.toInt()
        }

        setMeasuredDimension(width, height)
    }

}