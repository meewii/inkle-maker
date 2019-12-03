package com.gdt.inklemaker.di

import com.gdt.inklemaker.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

  @ContributesAndroidInjector
  abstract fun contributeMainActivity(): MainActivity

}