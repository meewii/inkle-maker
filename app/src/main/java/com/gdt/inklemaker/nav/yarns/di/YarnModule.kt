@file:Suppress("unused")

package com.gdt.inklemaker.nav.yarns.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdt.inklemaker.di.viewmodels.ViewModelKey
import com.gdt.inklemaker.nav.yarns.YarnListFragment
import com.gdt.inklemaker.nav.yarns.add.AddYarnViewModel
import com.gdt.inklemaker.nav.yarns.add.AddYarnFragment
import com.gdt.inklemaker.nav.yarns.add.YarnSizePickerFragment
import com.gdt.inklemaker.nav.yarns.business.YarnListViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [YarnViewModelBinderModule::class])
abstract class YarnFragmentModule {

  @ContributesAndroidInjector(modules = [YarnViewModelModule::class])
  internal abstract fun contributeStatsFragmentInjector(): YarnListFragment

  @ContributesAndroidInjector(modules = [AddYarnViewModelModule::class])
  internal abstract fun contributeYarnAddFragmentInjector(): AddYarnFragment

  @ContributesAndroidInjector
  internal abstract fun contributeYarnSizeChooserFragment(): YarnSizePickerFragment

}

@Module
class YarnViewModelModule {

  @Provides
  internal fun provideYarnListViewModel(fragment: YarnListFragment, factory: ViewModelProvider.Factory)
      : YarnListViewModel {
    return ViewModelProvider(fragment, factory).get(YarnListViewModel::class.java)
  }

}

@Module
class AddYarnViewModelModule {

  @Provides
  internal fun provideAddYarnViewModel(fragment: AddYarnFragment, factory: ViewModelProvider.Factory)
      : AddYarnViewModel {
    return ViewModelProvider(fragment, factory).get(AddYarnViewModel::class.java)
  }

}

@Module
abstract class YarnViewModelBinderModule {

  @Binds
  @IntoMap
  @ViewModelKey(YarnListViewModel::class)
  internal abstract fun bindYarnListViewModel(viewModel: YarnListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(AddYarnViewModel::class)
  internal abstract fun bindAddYarnViewModel(viewModel: AddYarnViewModel): ViewModel

}