package com.gdt.inklemaker.di

import android.app.Application
import android.content.Context
import com.gdt.inklemaker.InkleMakerApp
import com.gdt.inklemaker.di.viewmodels.ViewModelFactoryModule
import com.gdt.inklemaker.nav.inkles.di.InkleFragmentModule
import com.gdt.inklemaker.nav.yarns.di.YarnFragmentModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module(includes = [ViewModelFactoryModule::class, YarnFragmentModule::class, InkleFragmentModule::class])
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun application(app: InkleMakerApp): Application

    @Binds
    @Singleton
    abstract fun context(app: InkleMakerApp): Context
}
