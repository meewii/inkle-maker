package com.gdt.inklemaker.ui.patterns

/**
 * Model class for the hexagons drawn in the HexPatternView.
 *
 * The Hex model contains helper methods to know if a click event belong to that Hex or not.
 * In order to identify the position, it is decided that the Hex' 6 points are drawn starting
 * at p0 following this order:
 *
 *          p1
 *        / \
 *       /   \
 *    p0/     \p2
 *      |     |
 *      |     |
 *      |     |p3
 *    p5\     /
 *       \   /
 *        \ /
 *         p4
 */
data class Hex(
  val colorId: String,
  val row: Int,
  val column: Int,
  val vertexList: List<Point>,
  var isSelected: Boolean = false
) {

  fun isPointInside(testX: Float, testY: Float): Boolean {
    var isInside = false
    var i = 0
    var j = vertexList.size - 1

    while (i < vertexList.size) {
      if (vertexList[i].y > testY != vertexList[j].y > testY
        && testX < (vertexList[j].x - vertexList[i].x) * (testY - vertexList[i].y) / (vertexList[j].y - vertexList[i].y) + vertexList[i].x
      )
        isInside = !isInside

      j = i++
    }
    return isInside
  }

}

data class HexPath(val p0: Point, val p1: Point, val p2: Point, val p3: Point, val p4: Point, val p5: Point)

data class Quadrangle(
  val colorId: String,
  val row: Int,
  val column: Int,
  val path: QuadranglePath,
  var isSelected: Boolean = false
) {

  fun isPointInside(p: Point): Boolean {
    val area = path.p0.x * path.p1.y +
        path.p1.x * path.p2.y +
        path.p2.x * path.p3.y +
        path.p3.x * path.p0.y -
        path.p0.y * path.p1.x -
        path.p1.y * path.p2.x -
        path.p2.y * path.p3.x -
        path.p3.y * path.p0.x

    var isPointInside = true
    for (i in 0..4) {
      val testArray = arrayListOf(path.p0, path.p1, path.p2, path.p3)
      testArray.add(i, p)

      val testedArea = testArray[0].x * testArray[1].y +
          testArray[1].x * testArray[2].y +
          testArray[2].x * testArray[3].y +
          testArray[3].x * testArray[4].y +
          testArray[4].x * testArray[0].y -
          testArray[0].y * testArray[1].x -
          testArray[1].y * testArray[2].x -
          testArray[2].y * testArray[3].x -
          testArray[3].y * testArray[4].x -
          testArray[4].y * testArray[0].x

      if (testedArea >= area) {
        isPointInside = false
      }
    }

    return isPointInside
  }

}

data class QuadranglePath(val p0: Point, val p1: Point, val p2: Point, val p3: Point)

data class Point(val x: Float, val y: Float)