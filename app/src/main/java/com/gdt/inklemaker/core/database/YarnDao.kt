package com.gdt.inklemaker.core.database

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.gdt.inklemaker.R
import io.reactivex.Single

@Dao
interface YarnDao {

  companion object {
    const val YARN_TABLE_NAME = "YARN"
    const val YARN_COLUMN_ID = "YARN_ID"
    const val YARN_COLUMN_NAME = "YARN_NAME"
    const val YARN_COLUMN_COLOR = "YARN_COLOR"
    const val YARN_COLUMN_SIZE = "YARN_SIZE"
    const val YARN_COLUMN_CATEGORY_ID = "YARN_CATEGORY_ID"
  }

  @Query("SELECT * FROM $YARN_TABLE_NAME WHERE $YARN_COLUMN_ID = :id")
  fun getRow(id: String): LiveData<Yarn>

  @Query("SELECT * FROM $YARN_TABLE_NAME")
  fun getAll(): LiveData<List<Yarn>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(yarn: Yarn): Single<Unit>

  @Query("DELETE FROM $YARN_TABLE_NAME WHERE $YARN_COLUMN_ID = :id")
  fun delete(id: String): Single<Int>

}

@Entity(tableName = YarnDao.YARN_TABLE_NAME)
data class Yarn(
  @PrimaryKey @ColumnInfo(name = YarnDao.YARN_COLUMN_ID) var id: String,
  @ColumnInfo(name = YarnDao.YARN_COLUMN_NAME) var name: String,
  @ColumnInfo(name = YarnDao.YARN_COLUMN_COLOR) var color: String,
  @ColumnInfo(name = YarnDao.YARN_COLUMN_SIZE) var size: YarnSize = YarnSize.M,
  @ColumnInfo(name = YarnDao.YARN_COLUMN_CATEGORY_ID) var categoryId: Int? = null
) {
  @Ignore var isSelected: Boolean = false
}

enum class YarnSize(@StringRes val label: Int, @DrawableRes val icon: Int, @DrawableRes val iconTail: Int) {
  S(R.string.yarn_size_s, R.drawable.ic_yarn_s, R.drawable.ic_yarn_tail_s),
  M(R.string.yarn_size_m, R.drawable.ic_yarn_m, R.drawable.ic_yarn_tail_m),
  L(R.string.yarn_size_l, R.drawable.ic_yarn_l, R.drawable.ic_yarn_tail_l);
}