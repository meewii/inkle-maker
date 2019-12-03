package com.gdt.inklemaker.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.gdt.inklemaker.core.database.InkleDao
import com.gdt.inklemaker.core.database.InkleMakerDb
import com.gdt.inklemaker.core.database.YarnDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    @Singleton
    internal fun provideSharedPreferences(app: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    @Singleton
    fun provideDatabase(application: Application): InkleMakerDb =
        Room
            .databaseBuilder(application, InkleMakerDb::class.java, "InkleMaker.db")
            .fallbackToDestructiveMigration() // TODO: Remove for release
            .build()

    @Provides
    @Singleton
    fun provideYarnDao(database: InkleMakerDb): YarnDao = database.yarnDao()

    @Provides
    @Singleton
    fun provideInkleDao(database: InkleMakerDb): InkleDao = database.inkleDao()
}