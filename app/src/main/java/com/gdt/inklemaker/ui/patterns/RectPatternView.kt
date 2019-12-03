@file:Suppress("PrivatePropertyName")

package com.gdt.inklemaker.ui.patterns

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet


@SuppressLint("ViewConstructor")
class RectPatternView @JvmOverloads constructor(
    colorMap: Map<String, Int>, // Map<Color Id, Color-Argb>
    private val upperLineSequence: ArrayList<String>,
    private val lowerLineSequence: ArrayList<String>,
    private val res: RectPatternViewResHolder,
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BasePatternView(colorMap, res, context, attrs, defStyleAttr) {

    override var scale: Float = 1f

    override val doubleRowCount = 1

    override val totalWidth: Float
        get() {
            return if (upperLineSequence.size > lowerLineSequence.size) {
                upperLineSequence.size * res.CELL_WIDTH
            } else {
                upperLineSequence.size * res.CELL_WIDTH + res.RECT_LOWER_LINE_INDENT
            }
        }

    override val totalHeight: Float
        get() = res.CELL_HEIGHT * 2

    override fun drawUpperLine(canvas: Canvas, row: Int) {
        canvas.apply {
            upperLineSequence.forEachIndexed { i, cellId ->
                val rect = RectF()
                rect.apply {
                    top = 0f * row * res.CELL_HEIGHT
                    left = i * res.CELL_WIDTH
                    right = res.CELL_WIDTH + left
                    bottom = top + res.CELL_HEIGHT
                }
                drawRect(rect, paintMap[cellId] ?: error("Paint for cellId $cellId cannot be null"))
                drawRoundRect(rect, res.CELL_BORDER_RADIUS, res.CELL_BORDER_RADIUS, res.paintBorder)
            }
        }
    }

    override fun drawLowerLine(canvas: Canvas, row: Int) {
        canvas.apply {
            lowerLineSequence.forEachIndexed { i, cellId ->
                val rect = RectF()
                rect.apply {
                    top = res.CELL_HEIGHT + (row * res.CELL_HEIGHT)
                    left = i * res.CELL_WIDTH + res.RECT_LOWER_LINE_INDENT
                    right = res.CELL_WIDTH + left
                    bottom = top + res.CELL_HEIGHT
                }
                drawRect(rect, paintMap[cellId] ?: error("Paint for cellId $cellId cannot be null"))
                drawRoundRect(rect, res.CELL_BORDER_RADIUS, res.CELL_BORDER_RADIUS, res.paintBorder)
            }
        }
    }

}