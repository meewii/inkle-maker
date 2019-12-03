package com.gdt.inklemaker.nav.inkles

import com.gdt.inklemaker.ui.patterns.Hex
import com.gdt.inklemaker.ui.patterns.Point
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HexHelperTest {

  private lateinit var hexagon: Hex
  private lateinit var vertexX: FloatArray
  private lateinit var vertexY: FloatArray

  @Before
  fun setUp() {
    hexagon = Hex(
      "1", 0, 0,
      listOf(
        Point(0f, 30f),
        Point(20f, 0f),
        Point(40f, 30f),
        Point(40f, 70f),
        Point(20f, 100f),
        Point(0f, 70f)
      )
    )
    vertexX = hexagon.vertexList.map { it.x.toFloat() }.toFloatArray()
    vertexY = hexagon.vertexList.map { it.y.toFloat() }.toFloatArray()
  }

  @Test
  fun test_isInside_false() {
    var isInside = hexagon.isPointInside(1f, 1f)
    Assert.assertFalse(isInside)

    isInside = hexagon.isPointInside(29f, 1f)
    Assert.assertFalse(isInside)

    isInside = hexagon.isPointInside(29f, 99f)
    Assert.assertFalse(isInside)

    isInside = hexagon.isPointInside(1f, 99f)
    Assert.assertFalse(isInside)
  }

  @Test
  fun test_isInsideAPolygon_true() {
    var isInside = hexagon.isPointInside(10f, 60f)
    Assert.assertTrue(isInside)

    isInside = hexagon.isPointInside(12f, 28f)
    Assert.assertTrue(isInside)

    isInside = hexagon.isPointInside(8f, 75f)
    Assert.assertTrue(isInside)
  }

}