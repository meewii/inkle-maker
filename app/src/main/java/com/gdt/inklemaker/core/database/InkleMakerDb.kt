package com.gdt.inklemaker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Yarn::class, Inkle::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class InkleMakerDb : RoomDatabase() {

  abstract fun yarnDao(): YarnDao
  abstract fun inkleDao(): InkleDao

}