@file:Suppress("MemberVisibilityCanBePrivate", "PropertyName")

package com.gdt.inklemaker.ui.patterns

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.gdt.inklemaker.R

open class PatternViewResHolder(context: Context) {

    val CELL_BORDER_SIZE =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_cell_border_size)
            .toFloat()
    val CELL_BORDER_SELECTED_SIZE =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_cell_border_selected_size)
            .toFloat()
    val COLOR_WHITE = ContextCompat.getColor(context, R.color.white)
    val COLOR_BLACK = ContextCompat.getColor(context, R.color.black)
    val COLOR_PINK = ContextCompat.getColor(context, R.color.pink)

    val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_WHITE
        strokeWidth = CELL_BORDER_SIZE
        pathEffect = CornerPathEffect(CELL_BORDER_SIZE)
    }

    val paintBorderSelected = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = COLOR_BLACK
        strokeWidth = CELL_BORDER_SELECTED_SIZE
        pathEffect = CornerPathEffect(CELL_BORDER_SELECTED_SIZE)
    }

    val paintPink = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = COLOR_PINK
        pathEffect = CornerPathEffect(CELL_BORDER_SIZE)
    }

}

/**
 * Constants for HexPatternView
 */
class HexPatternViewResHolder(context: Context) : PatternViewResHolder(context) {
    val HEX_Y_1 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_y_1)
    val HEX_Y_2 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_y_2)
    val HEX_Y_3 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_y_3)
    val HEX_Y_4 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_y_4)
    val HEX_X_1 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_x_1)
    val HEX_X_2 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_x_2)
    val HEX_X_3 = context.resources.getDimensionPixelSize(R.dimen.hex_cell_point_x_3)
    val HEX_WIDTH = context.resources.getDimensionPixelSize(R.dimen.hex_cell_width)
    val HEX_HEIGHT = context.resources.getDimensionPixelSize(R.dimen.hex_cell_height)
    val HEX_PADDING_TOP = context.resources.getDimensionPixelSize(R.dimen.hex_view_padding_top)
    val HEX_PADDING_LEFT = context.resources.getDimensionPixelSize(R.dimen.hex_view_padding_left)
    val HEX_LOWER_LINE_INDENT =
        context.resources.getDimensionPixelSize(R.dimen.hex_cell_lower_row_indent)
    val MAX_SCROLL_DISTANCE: Int = context.resources.getDimensionPixelSize(R.dimen.hex_view_max_scroll)

    val HEX_SHADOW: Drawable? = ContextCompat.getDrawable(context, R.drawable.img_hex_shadow)

    val QUADRANGLE_Y_1 = context.resources.getDimensionPixelSize(R.dimen.hex_weft_cell_point_y_1)
    val QUADRANGLE_Y_2 = context.resources.getDimensionPixelSize(R.dimen.hex_weft_cell_point_y_2)
    val QUADRANGLE_Y_3 = context.resources.getDimensionPixelSize(R.dimen.hex_weft_cell_point_y_3)
    val QUADRANGLE_X_1 = context.resources.getDimensionPixelSize(R.dimen.hex_weft_cell_point_x_1)
    val QUADRANGLE_X_2 = context.resources.getDimensionPixelSize(R.dimen.hex_weft_cell_point_x_2)
}

/**
 * Constants for RectPatternView
 */
class RectPatternViewResHolder(context: Context) : PatternViewResHolder(context) {

    val CELL_WIDTH =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_cell_width).toFloat()
    val CELL_HEIGHT =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_cell_height).toFloat()
    val CELL_BORDER_RADIUS =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_cell_border_radius).toFloat()
    val RECT_LOWER_LINE_INDENT =
        context.resources.getDimensionPixelSize(R.dimen.pattern_view_lower_line_indent).toFloat()

}
