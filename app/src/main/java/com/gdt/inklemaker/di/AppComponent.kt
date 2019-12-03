@file:Suppress("unused")

package com.gdt.inklemaker.di

import com.gdt.inklemaker.InkleMakerApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class, AppModule::class,
        ActivityModule::class, StorageModule::class]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: InkleMakerApp): Builder

        fun build(): AppComponent
    }

    fun inject(app: InkleMakerApp)

}
