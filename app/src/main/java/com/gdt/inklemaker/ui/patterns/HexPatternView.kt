@file:Suppress("PrivatePropertyName")

package com.gdt.inklemaker.ui.patterns

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.WindowManager
import kotlin.math.round
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class HexPatternView @JvmOverloads constructor(
  colorMap: Map<String, Int>, // Map<Color Id, Color-Argb>
  val upperYarnIds: ArrayList<String>,
  val lowerYarnIds: ArrayList<String>,
  var weftYarnId: String?,
  val res: HexPatternViewResHolder,
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BasePatternView(colorMap, res, context, attrs, defStyleAttr) {

  override var scale: Float = 1f
  override var doubleRowCount = 8

  override var totalWidth: Float = totalWidth()
  override var totalHeight: Float = totalHeight()
  var widthSize: Int = 0
  var heightSize: Int = 0

  override fun onDraw(canvas: Canvas) {
    scaleValues(scale)
    // the weft polygons are drawn
    drawWeft(canvas, weftYarnId)
    // all the hexagons are drawn
    super.onDraw(canvas)
    // The selected hexagons and quadrangles are drawn again on top of the other for full borders
    drawSelected(canvas)
  }

  private var hexY1 = res.HEX_Y_1 * scale
  private var hexY2 = res.HEX_Y_2 * scale
  private var hexY3 = res.HEX_Y_3 * scale
  private var hexY4 = res.HEX_Y_4 * scale
  private var hexX1 = res.HEX_X_1 * scale
  private var hexX2 = res.HEX_X_2 * scale
  private var hexX3 = res.HEX_X_3 * scale
  private var quadrangleY1 = res.QUADRANGLE_Y_1 * scale
  private var quadrangleY2 = res.QUADRANGLE_Y_2 * scale
  private var quadrangleY3 = res.QUADRANGLE_Y_3 * scale
  private var quadrangleX1 = res.QUADRANGLE_X_1 * scale
  private var quadrangleX2 = res.QUADRANGLE_X_2 * scale
  private var hexWidth = res.HEX_WIDTH * scale
  private var hexHeight = res.HEX_HEIGHT * scale
  private var hexTriangleHeight = res.HEX_HEIGHT * scale / 4
  private var hexPaddingTop = res.HEX_PADDING_TOP * scale
  private var hexPaddingLeft = res.HEX_PADDING_LEFT * scale
  private var maxScrollDistance = res.MAX_SCROLL_DISTANCE * scale
  private var lowerLineIndent = res.HEX_LOWER_LINE_INDENT * scale

  /**
   * Map of all the hexagons created in the view. There are as many as
   * doubleRowCount * upperLineSequence + doubleRowCount * lowerLineSequence
   */
  private val hexMap: MutableMap<String, Hex> = mutableMapOf()
  private val quadrangleMap: MutableMap<String, Quadrangle> = mutableMapOf()

  /**
   * Current column that is selected by the user, list because there can be several columns
   * selected at the same time
   */
  var selectedColumns: ArrayList<Int> = arrayListOf()

  /**
   * The weft is just one thread so if it selected, all the quadrangles are selected
   */
  var isWeftSelected: Boolean = false

  /**
   * Is true if the view is currently being drawn in order to generate a preview image
   */
  private var isRedrawingForPreview = false

  /**
   * Every time the canvas is invalidated after scaling, we recalculate all the positions and
   * paddings.
   */
  private fun scaleValues(scale: Float) {
    // scale the hex sides and position
    hexY1 = res.HEX_Y_1 * scale
    hexY2 = res.HEX_Y_2 * scale
    hexY3 = res.HEX_Y_3 * scale
    hexY4 = res.HEX_Y_4 * scale
    hexX1 = res.HEX_X_1 * scale
    hexX2 = res.HEX_X_2 * scale
    hexX3 = res.HEX_X_3 * scale
    quadrangleY1 = res.QUADRANGLE_Y_1 * scale
    quadrangleY2 = res.QUADRANGLE_Y_2 * scale
    quadrangleY3 = res.QUADRANGLE_Y_3 * scale
    quadrangleX1 = res.QUADRANGLE_X_1 * scale
    quadrangleX2 = res.QUADRANGLE_X_2 * scale
    hexWidth = res.HEX_WIDTH * scale
    hexHeight = res.HEX_HEIGHT * scale
    hexTriangleHeight = res.HEX_HEIGHT * scale / 4
    if (isRedrawingForPreview) {
      hexPaddingTop = 0f
      hexPaddingLeft = 0f
    } else {
      hexPaddingTop = res.HEX_PADDING_TOP * scale
      hexPaddingLeft = res.HEX_PADDING_LEFT * scale
    }
    maxScrollDistance = res.MAX_SCROLL_DISTANCE * scale
    lowerLineIndent = res.HEX_LOWER_LINE_INDENT * scale
    // scale the hex borders
    cellBorderSize = res.CELL_BORDER_SIZE * scale
    cellBorderSelectedSize = res.CELL_BORDER_SELECTED_SIZE * scale
    res.paintBorder.strokeWidth = cellBorderSize
    res.paintBorderSelected.strokeWidth = cellBorderSelectedSize

    totalWidth = totalWidth()
    totalHeight = totalHeight()
  }

  private fun totalWidth(): Float = if (upperYarnIds.size > lowerYarnIds.size) {
    upperYarnIds.size * hexWidth + hexPaddingLeft * 2
  } else {
    upperYarnIds.size * hexWidth + lowerLineIndent + hexPaddingLeft * 2
  }

  private fun totalHeight(): Float =
    doubleRowCount * (hexHeight * 2 - hexTriangleHeight * 2) + hexTriangleHeight + hexPaddingTop * 2

  private val hasEvenWarp = upperYarnIds.size == lowerYarnIds.size

  /**
   * Container for the Hex shadow image
   */
  private val hexShadowRect = Rect()

  private var hexShadowBitmap: Bitmap? = drawableToBitmap(res.HEX_SHADOW)

  /**
   * Draws the weft of the pattern
   * The weft is drawn once per row and alternatively left and right of a line
   */
  private fun drawWeft(canvas: Canvas, weftYarnId: String?) {
    canvas.apply {
      for (row in 0 until doubleRowCount) {
        val isLast = row == doubleRowCount - 1
        val colorId = weftYarnId ?: upperYarnIds.first()
        val paint = paintMap[colorId]
          ?: error("Paint for weftId $colorId cannot be null")
        val columnLeftId = 0
        val columnRightId = 1

        // draw left weft
        val xLeftPadding = hexPaddingLeft
        val yLeftPadding =
          row * (hexHeight * 2 - hexTriangleHeight * 2) + hexPaddingTop + hexHeight - hexTriangleHeight
        val leftWeft = createWeft(colorId, row, columnLeftId, xLeftPadding.toInt(), yLeftPadding.toInt())
        drawQuadrangle(canvas, paint, leftWeft)

        val leftWeftId = "${row}-${columnLeftId}"
        quadrangleMap[leftWeftId] = leftWeft

        if (isLast) return

        // draw right weft
        val xRightPadding = totalWidth - hexWidth / 2 - hexPaddingLeft
        val yRightPadding = if (upperYarnIds.size > lowerYarnIds.size) {
          (hexHeight + hexTriangleHeight * 2) + (row * (hexHeight * 2 - hexTriangleHeight * 2))
        } else {
          (hexHeight - hexTriangleHeight) + (row * (hexHeight * 2 - hexTriangleHeight * 2)) + hexPaddingTop +
              hexHeight - hexTriangleHeight
        }
        val rightWeft = createWeft(colorId, row, columnRightId, xRightPadding.toInt(), yRightPadding.toInt())
        drawQuadrangle(canvas, paint, rightWeft)

        val rightWeftId = "${row}-${columnRightId}"
        quadrangleMap[rightWeftId] = rightWeft
      }
    }
  }

  /**
   * Draws the upper line of the pattern as many times as defined by `doubleRowCount`
   */
  override fun drawUpperLine(canvas: Canvas, row: Int) {
    canvas.apply {
      upperYarnIds.forEachIndexed { i, cellId ->
        val paint = paintMap[cellId] ?: error("Paint for cellId $cellId cannot be null")
        val xPadding = i * hexWidth + hexPaddingLeft
        val yPadding = row * (hexHeight * 2 - hexTriangleHeight * 2) + hexPaddingTop
        val rowId = row * 2
        val columnId = i * 2
        val hexId = "${rowId}-${columnId}"

        val hex = createHex(cellId, rowId, columnId, xPadding.toInt(), yPadding.toInt())
        hexMap[hexId] = hex

        drawHex(canvas, paint, hex)
      }
    }
  }

  /**
   * Draws the lower line of the pattern as many times as defined by `doubleRowCount`
   */
  override fun drawLowerLine(canvas: Canvas, row: Int) {
    canvas.apply {
      lowerYarnIds.forEachIndexed { i, cellId ->
        val paint = paintMap[cellId] ?: error("Paint for cellId $cellId cannot be null")
        val xPadding = i * hexWidth + lowerLineIndent + hexPaddingLeft
        val yPadding =
          (hexHeight - hexTriangleHeight) + (row * (hexHeight * 2 - hexTriangleHeight * 2)) + hexPaddingTop
        val rowId = row * 2 + 1
        val columnId = i * 2 + 1
        val hexId = "${rowId}-${columnId}"

        val hex = createHex(cellId, rowId, columnId, xPadding.toInt(), yPadding.toInt())
        hexMap[hexId] = hex

        drawHex(canvas, paint, hex)
      }
    }
  }

  /**
   * Draws only the hexagons that were selected
   */
  private fun drawSelected(canvas: Canvas) {
    canvas.apply {
      hexMap.values.forEach { hex ->
        if (hex.isSelected) {
          val paint =
            paintMap[hex.colorId] ?: error("Paint for colorId ${hex.colorId} cannot be null")
          drawHex(canvas, paint, hex)
        }
      }
      if (isWeftSelected) {
        quadrangleMap.values.forEach { weft ->
          val paint =
            paintMap[weft.colorId] ?: error("Paint for colorId ${weft.colorId} cannot be null")
          drawQuadrangle(canvas, paint, weft)
        }
      }
    }
  }

  private fun createHex(cellId: String, row: Int, column: Int, xPadding: Int, yPadding: Int)
      : Hex {
    val vertexList: ArrayList<Point> = arrayListOf()
    vertexList.add(Point(hexX1 + xPadding, hexY2 + yPadding))
    vertexList.add(Point(hexX2 + xPadding, hexY1 + yPadding))
    vertexList.add(Point(hexX3 + xPadding, hexY2 + yPadding))
    vertexList.add(Point(hexX3 + xPadding, hexY3 + yPadding))
    vertexList.add(Point(hexX2 + xPadding, hexY4 + yPadding))
    vertexList.add(Point(hexX1 + xPadding, hexY3 + yPadding))

    return Hex(cellId, row, column, vertexList, isSelected = selectedColumns.contains(column))
  }

  private fun drawHex(canvas: Canvas, paint: Paint, hex: Hex) {
    val path = Path()
    path.moveTo(hex.vertexList[0].x, hex.vertexList[0].y)
    path.lineTo(hex.vertexList[1].x, hex.vertexList[1].y)
    path.lineTo(hex.vertexList[2].x, hex.vertexList[2].y)
    path.lineTo(hex.vertexList[3].x, hex.vertexList[3].y)
    path.lineTo(hex.vertexList[4].x, hex.vertexList[4].y)
    path.lineTo(hex.vertexList[5].x, hex.vertexList[5].y)
    path.close()

    canvas.drawPath(path, paint)

    hexShadowBitmap?.let {
      hexShadowRect.apply {
        left = hex.vertexList[0].x.toInt()
        right = hex.vertexList[2].x.toInt()
        top = hex.vertexList[1].y.toInt()
        bottom = hex.vertexList[4].y.toInt()
      }
      canvas.drawBitmap(it, null, hexShadowRect, null)
    }

    canvas.drawPath(
      path, if (hex.isSelected) res.paintBorderSelected
      else res.paintBorder
    )
  }

  private fun createWeft(cellId: String, row: Int, column: Int, xPadding: Int, yPadding: Int)
      : Quadrangle {
    val p0: Point
    val p1: Point
    val p2: Point
    val p3: Point
    if (column == 0 || !hasEvenWarp) {
      p0 = Point(quadrangleX1 + xPadding, quadrangleY1 + yPadding)
      p1 = Point(quadrangleX2 + xPadding, quadrangleY2 + yPadding)
      p2 = Point(quadrangleX2 + xPadding, quadrangleY3 + yPadding)
      p3 = Point(quadrangleX1 + xPadding, quadrangleY2 + yPadding)
    } else {
      p0 = Point(quadrangleX1 + xPadding, quadrangleY2 + yPadding)
      p1 = Point(quadrangleX2 + xPadding, quadrangleY1 + yPadding)
      p2 = Point(quadrangleX2 + xPadding, quadrangleY2 + yPadding)
      p3 = Point(quadrangleX1 + xPadding, quadrangleY3 + yPadding)
    }
    return Quadrangle(cellId, row, column, QuadranglePath(p0, p1, p2, p3), isWeftSelected)
  }

  private fun drawQuadrangle(canvas: Canvas, paint: Paint, quadrangle: Quadrangle) {
    val path = Path()
    path.moveTo(quadrangle.path.p0.x, quadrangle.path.p0.y)
    path.lineTo(quadrangle.path.p1.x, quadrangle.path.p1.y)
    path.lineTo(quadrangle.path.p2.x, quadrangle.path.p2.y)
    path.lineTo(quadrangle.path.p3.x, quadrangle.path.p3.y)
    path.close()

    canvas.drawPath(path, paint)
    canvas.drawPath(
      path, if (quadrangle.isSelected) res.paintBorderSelected
      else res.paintBorder
    )
  }

  /**
   * Called to determine the size requirements for this view and all of its children.
   */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)
    this.widthSize = widthSize
    this.heightSize = heightSize
    setMeasuredDimension(widthSize, heightSize)
  }

  /**
   * Zoom gesture listener
   */
  private val scaleGestureListener =
    object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

      var maxScale: Float? = null
      var minScale: Float? = null

      override fun onScale(detector: ScaleGestureDetector?): Boolean {
        detector?.let {
          scale *= detector.scaleFactor

          // Limit the zoom with max and min values
          when {
            scale > 2.0 -> {
              if (maxScale == null) maxScale = scale
              maxScale?.let { scale = it }
            }
            scale < 0.2 -> {
              if (minScale == null) minScale = scale
              minScale?.let { scale = it }
            }
            else -> {
              invalidate()
              maxScale = null
              minScale = null
            }
          }
        }
        return true
      }

    }

  // the distance the view was moved on X axis
  private var totalDistanceX = 0

  // the distance the view was moved on Y axis
  private var totalDistanceY = 0

  /**
   * Tap and scroll gesture listener
   */
  private val gestureListener = object : GestureListeners() {

    // the total distance the finger has moved on X axis
    private var scrollX = 0
    // the previous total distance the finger has moved on X axis
    private var previousScrollX = 0
    private var isScrollingLeft = false

    // the total distance the finger has moved on Y axis
    private var scrollY = 0
    // the previous total distance the finger has moved on Y axis
    private var previousScrollY = 0
    private var isScrollingUp = false

    // Select a hexagon or a quadrangle
    override fun onSingleTapListener(point: Point) {
      val selected = hexMap.values.find {
        it.isPointInside(point.x + totalDistanceX, point.y + totalDistanceY)
      }

      val weftSelected = quadrangleMap.values.any {
        it.isPointInside(Point(point.x + totalDistanceX, point.y + totalDistanceY))
      }

      if (selected == null && !weftSelected) {
        isWeftSelected = false
        selectedColumns.clear()
      } else if (selected != null) {
        if (selectedColumns.contains(selected.column)) selectedColumns.remove(selected.column)
        else selectedColumns.add(selected.column)
      } else {
        isWeftSelected = !isWeftSelected
      }

      invalidate()
    }

    // Reset the scale and distance
    override fun onDoubleTapListener() {
      scale = 1f
      invalidate()

      scrollBy(-totalDistanceX, -totalDistanceY)
      totalDistanceX = 0
      totalDistanceY = 0
    }

    /**
     * Scrolls the view horizontally and vertically following the dragging of the finger.
     * The scroll is limited to max top/left/bottom/right
     */
    override fun onScrollListener(distanceX: Float, distanceY: Float) {

      // find if user is scrolling Left
      scrollX += distanceX.roundToInt()
      isScrollingLeft = previousScrollX < scrollX
      previousScrollX = scrollX

      val maxLeft = totalWidth + hexPaddingLeft - maxScrollDistance
      val maxRight = widthSize * -1 + maxScrollDistance + hexPaddingLeft

      // Distance allowed to scroll on axis X
      val scrollByX: Int = when {
        totalDistanceX > maxLeft -> {
          if (!isScrollingLeft) {
            totalDistanceX += distanceX.roundToInt()
            distanceX.roundToInt()
          } else 0
        }
        totalDistanceX < maxRight -> {
          if (isScrollingLeft) {
            totalDistanceX += distanceX.roundToInt()
            distanceX.roundToInt()
          } else 0
        }
        else -> {
          totalDistanceX += distanceX.roundToInt()
          distanceX.roundToInt()
        }
      }

      // find if user is scrolling Up
      scrollY += distanceY.roundToInt()
      isScrollingUp = previousScrollY < scrollY
      previousScrollY = scrollY

      val maxTop = totalHeight - maxScrollDistance
      val maxBottom = heightSize * -1 + maxScrollDistance + hexPaddingTop

      // Distance allowed to scroll on axis Y
      val scrollByY: Int = when {
        totalDistanceY > maxTop -> {
          if (!isScrollingUp) {
            totalDistanceY += distanceY.roundToInt()
            distanceY.roundToInt()
          } else 0
        }
        totalDistanceY < maxBottom -> {
          if (isScrollingUp) {
            totalDistanceY += distanceY.roundToInt()
            distanceY.roundToInt()
          } else 0
        }
        else -> {
          totalDistanceY += distanceY.roundToInt()
          distanceY.roundToInt()
        }
      }

      scrollBy(scrollByX, scrollByY)
    }
  }

  private var scaleDetector = ScaleGestureDetector(context, scaleGestureListener)
  private var detector = GestureDetector(context, gestureListener)

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    detector.onTouchEvent(event)
    scaleDetector.onTouchEvent(event)
    return true
  }

  /**
   * Reset the position and set values to match the size of the preview Bitmap that will be generated from this view
   */
  fun redrawForImage() {
    isRedrawingForPreview = true
    val metrics = getDisplayMetrics()

    hexMap.clear()
    quadrangleMap.clear()
    selectedColumns.clear()
    isWeftSelected = false

    // reset
    scale = 1f
    scaleValues(scale)

    scrollBy(-totalDistanceX, -totalDistanceY)
    totalDistanceX = 0
    totalDistanceY = 0

    // set the scale to fit half of the screen
    scale = metrics.widthPixels / 2 / totalWidth
    scaleValues(scale)

    val line = hexHeight * 2 - hexTriangleHeight * 2
    val maxLines = metrics.widthPixels / 2 / line
    val rowCount = round(maxLines).toInt()

    doubleRowCount = if (rowCount > 1) rowCount else 2
    scaleValues(scale)

    invalidate()
  }

  private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    if (drawable is BitmapDrawable) return drawable.bitmap

    val bitmap =
      Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  private fun getDisplayMetrics(): DisplayMetrics {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(metrics)
    return metrics
  }

}