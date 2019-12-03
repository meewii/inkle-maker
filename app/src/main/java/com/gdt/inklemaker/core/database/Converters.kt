package com.gdt.inklemaker.core.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

  @TypeConverter
  fun fromYarnSize(value: YarnSize): String {
    return value.toString()
  }

  @TypeConverter
  fun toYarnSize(value: String?): YarnSize? {
    return value?.let { YarnSize.valueOf(value) } ?: YarnSize.M
  }

  @TypeConverter
  fun fromWeavingTechnique(value: WeavingTechnique): String {
    return value.toString()
  }

  @TypeConverter
  fun toWeavingTechnique(value: String?): WeavingTechnique? {
    return value?.let { WeavingTechnique.valueOf(value) } ?: WeavingTechnique.PLAIN
  }

  @TypeConverter
  fun fromStringToYarns(json: String?): List<Yarn>? {
    val listType: Type = object : TypeToken<List<Yarn>?>() {}.type
    return Gson().fromJson(json, listType)
  }

  @TypeConverter
  fun fromYarnList(list: List<Yarn>?): String? {
    return Gson().toJson(list)
  }

  @TypeConverter
  fun fromStringToIdList(json: String?): ArrayList<String>? {
    val listType: Type = object : TypeToken<ArrayList<String>?>() {}.type
    return Gson().fromJson(json, listType)
  }

  @TypeConverter
  fun fromIdArrayList(list: ArrayList<String>?): String? {
    return Gson().toJson(list)
  }
}