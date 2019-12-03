package com.gdt.inklemaker.nav.inkles

import com.gdt.inklemaker.ui.patterns.Point
import com.gdt.inklemaker.ui.patterns.Quadrangle
import com.gdt.inklemaker.ui.patterns.QuadranglePath
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class QuadrangleHelperTest {

  private lateinit var quadrangle: Quadrangle

  @Before
  fun setUp() {
    quadrangle = Quadrangle(
      colorId = "1", row = 0, column = 0,
      path = QuadranglePath(
        p0 = Point(0f, 0f),
        p1 = Point(60f, 100f),
        p2 = Point(60f, 200f),
        p3 = Point(0f, 100f)
      )
    )
  }

  @Test
  fun test_isInside_true() {
    var isInside = quadrangle.isPointInside(Point(10f, 30f))
    Assert.assertTrue(isInside)

    isInside = quadrangle.isPointInside(Point(20f, 50f))
    Assert.assertTrue(isInside)

    isInside = quadrangle.isPointInside(Point(50f, 90f))
    Assert.assertTrue(isInside)

    isInside = quadrangle.isPointInside(Point(50f, 180f))
    Assert.assertTrue(isInside)
  }

  @Test
  fun test_isInside_false() {
    var isInside = quadrangle.isPointInside(Point(60f, 90f))
    Assert.assertFalse(isInside)

    isInside = quadrangle.isPointInside(Point(0f, 170f))
    Assert.assertFalse(isInside)

    isInside = quadrangle.isPointInside(Point(10f, 120f))
    Assert.assertFalse(isInside)

    isInside = quadrangle.isPointInside(Point(70f, 100f))
    Assert.assertFalse(isInside)

    isInside = quadrangle.isPointInside(Point(-7f, 100f))
    Assert.assertFalse(isInside)
  }

}