package com.gdt.inklemaker.core.database

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.gdt.inklemaker.core.database.InkleDao.Companion.INKLE_COLUMN_CREATED_AT
import com.gdt.inklemaker.core.database.InkleDao.Companion.INKLE_COLUMN_NAME
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

@Dao
interface InkleDao {

  companion object {
    const val INKLE_TABLE_NAME = "INKLE"
    const val INKLE_COLUMN_ID = "INKLE_ID"
    const val INKLE_COLUMN_IS_COMPLETE = "INKLE_IS_COMPLETE"
    const val INKLE_COLUMN_NAME = "INKLE_NAME"
    const val INKLE_COLUMN_CREATED_AT = "INKLE_CREATED_AT"
  }

  @Query("SELECT * FROM $INKLE_TABLE_NAME WHERE $INKLE_COLUMN_ID = :id")
  fun getRow(id: String): Single<Inkle>

  @Query("SELECT * FROM $INKLE_TABLE_NAME")
  fun getAllLiveData(): LiveData<List<Inkle>>

  @Query("SELECT * FROM $INKLE_TABLE_NAME")
  fun getAll(): Flowable<List<Inkle>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(inkle: Inkle): Single<Unit>

  @Update
  fun update(inkle: Inkle): Single<Unit>

  @Query("DELETE FROM $INKLE_TABLE_NAME WHERE $INKLE_COLUMN_ID = :id")
  fun delete(id: String): Single<Int>

}

@Entity(tableName = InkleDao.INKLE_TABLE_NAME)
data class Inkle(
  @PrimaryKey @ColumnInfo(name = InkleDao.INKLE_COLUMN_ID) val id: String,
  @ColumnInfo(name = INKLE_COLUMN_NAME) var name: String,
  @ColumnInfo(name = INKLE_COLUMN_CREATED_AT) val createdAt: Long,
  var lengthCm: Int = 0,
  val yarns: List<Yarn>,
  var weftYarnId: String? = null,
  var upperWarpYarnIds: ArrayList<String> = arrayListOf(),
  var lowerWarpYarnIds: ArrayList<String> = arrayListOf(),
  @ColumnInfo(name = InkleDao.INKLE_COLUMN_IS_COMPLETE) var isComplete: Boolean = false
) {

  /**
   * Generated preview picture
   */
  var savedImagePath: String? = null
  @Ignore var savedImage: Bitmap? = null

  var type: WeavingTechnique = WeavingTechnique.PLAIN

  /**
   * Timestamp in milliseconds
   */
  var updatedAt: Long = -1

  private val warpYarnIds: List<String>
    get() = upperWarpYarnIds + lowerWarpYarnIds

  /**
   * Converts warpYarnIds sequence into an integer list pattern
   * Example: listOf("ID#3", "ID#2", "ID#1", "ID#1", "ID#2", "ID#3")
   * will become: listOf(0, 1, 2, 2, 1, 0)
   */
  fun generatePattern(): ArrayList<Int> {
    val map: HashMap<String, Int> = hashMapOf()
    val warpPattern = arrayListOf<Int>()
    var i = 0
    warpYarnIds.forEach { id ->
      val mapId = map[id]
      if (mapId != null) {
        warpPattern.add(mapId)
      } else {
        map[id] = i
        warpPattern.add(i)
        i++
      }
    }
    return warpPattern
  }

  /**
   * Creates a Map<Color Id, Color-Argb> from `yarns` list
   */
  fun generateColorMap(): Map<String, Int> {
    val map: HashMap<String, Int> = hashMapOf()
    yarns.forEach { yarn ->
      try {
        val color = Color.parseColor(yarn.color)
        map[yarn.id] = color
      } catch (e: Exception) {
        Timber.e(e)
      }
    }
    return map
  }
}

/**
 * Only PLAIN for V1
 */
enum class WeavingTechnique {
  PLAIN //, PICK_UP, CARDS
}