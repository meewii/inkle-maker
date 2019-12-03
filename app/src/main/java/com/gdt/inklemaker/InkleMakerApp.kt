package com.gdt.inklemaker

import android.app.Application
import com.gdt.inklemaker.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import meewii.core.LineNumberDebugTree
import timber.log.Timber
import javax.inject.Inject

class InkleMakerApp : Application(), HasAndroidInjector {

  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

  override fun androidInjector(): AndroidInjector<Any> {
    return dispatchingAndroidInjector
  }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(LineNumberDebugTree())
    }
    DaggerAppComponent.builder().application(this)
        .build()
        .inject(this)
  }

}