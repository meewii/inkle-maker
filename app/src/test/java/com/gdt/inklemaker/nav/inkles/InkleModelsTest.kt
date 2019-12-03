package com.gdt.inklemaker.nav.inkles

import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.core.database.YarnSize
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InkleModelsTest {

  private lateinit var inkles: List<Inkle>

  @Before
  fun setUp() {
    inkles = getInkleOnboardingList()
  }

  @Test
  fun test_generatedPattern() {
    Assert.assertEquals(listOf(0, 1, 2, 2, 1, 0, 0, 2, 1, 2, 0), inkles[0].generatePattern())
    Assert.assertEquals(
      listOf(0, 1, 1, 0, 2, 3, 2, 3, 3, 2, 3, 2, 0, 1, 1, 0, 0, 1, 0, 2, 3, 2, 0, 0, 0, 2, 3, 2, 0, 1, 0),
      inkles[1].generatePattern()
    )
  }

  private fun getInkleOnboardingList(): List<Inkle> {
    return arrayListOf<Inkle>().apply {
      add(
        Inkle(
          id = "1",
          name = "My first inkle",
          createdAt = 1566318345,
          lengthCm = 180,
          yarns = getYarnList1(),
          weftYarnId = "3",
          upperWarpYarnIds = arrayListOf("3", "2", "1", "1", "2", "3"),
          lowerWarpYarnIds = arrayListOf("3", "1", "2", "1", "3")
        )
      )
      add(
        Inkle(
          id = "2",
          name = "Hans' Inkle",
          createdAt = 1566318345,
          lengthCm = 120,
          yarns = getYarnList2(),
          weftYarnId = "4",
          upperWarpYarnIds = arrayListOf(
            "4",
            "6",
            "6",
            "4",
            "7",
            "5",
            "7",
            "5",
            "5",
            "7",
            "5",
            "7",
            "4",
            "6",
            "6",
            "4"
          ),
          lowerWarpYarnIds = arrayListOf("4", "6", "4", "7", "5", "7", "4", "4", "4", "7", "5", "7", "4", "6", "4")
        )
      )
    }
  }

  private fun getYarnList1(): List<Yarn> {
    return arrayListOf<Yarn>().apply {
      add(Yarn("1", "Willow Grove (Green)", "#6b7e5c", YarnSize.M, 1))
      add(Yarn("2", "Sage (Green)", "#99ad89", YarnSize.M, 1))
      add(Yarn("3", "Turkish Rose", "#ad6a78", YarnSize.M, 0))
    }
  }

  private fun getYarnList2(): List<Yarn> {
    return arrayListOf<Yarn>().apply {
      add(Yarn("4", "Yellow", "#e79927", YarnSize.M, 1))
      add(Yarn("5", "Orange", "#c85f1f", YarnSize.M, 1))
      add(Yarn("6", "Brown", "#6c300c", YarnSize.M, 0))
      add(Yarn("7", "Light gray", "#9b9f9f", YarnSize.M, 0))
    }
  }
}