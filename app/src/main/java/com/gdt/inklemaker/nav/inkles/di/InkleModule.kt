@file:Suppress("unused")

package com.gdt.inklemaker.nav.inkles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdt.inklemaker.di.viewmodels.ViewModelKey
import com.gdt.inklemaker.nav.inkles.add.draw.DrawInkleFragment
import com.gdt.inklemaker.nav.inkles.add.draw.DrawInkleViewModel
import com.gdt.inklemaker.nav.inkles.add.recap.RecapInkleFragment
import com.gdt.inklemaker.nav.inkles.add.recap.RecapInkleViewModel
import com.gdt.inklemaker.nav.inkles.add.setup.SetUpInkleFragment
import com.gdt.inklemaker.nav.inkles.add.setup.SetUpInkleViewModel
import com.gdt.inklemaker.nav.inkles.add.setup.YarnPickerFragment
import com.gdt.inklemaker.nav.inkles.add.setup.YarnPickerViewModel
import com.gdt.inklemaker.nav.inkles.list.InkleListFragment
import com.gdt.inklemaker.nav.inkles.list.InkleListViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [InkleViewModelBinderModule::class])
abstract class InkleFragmentModule {

  @ContributesAndroidInjector(modules = [InkleViewModelModule::class])
  internal abstract fun contributeInkleListFragmentInjector(): InkleListFragment

  @ContributesAndroidInjector(modules = [SetUpInkleViewModelModule::class])
  internal abstract fun contributeSetUpInkleFragmentInjector(): SetUpInkleFragment

  @ContributesAndroidInjector(modules = [DrawInkleViewModelModule::class])
  internal abstract fun contributeAddInkleFragmentInjector(): DrawInkleFragment

  @ContributesAndroidInjector(modules = [RecapInkleViewModelModule::class])
  internal abstract fun contributeRecapInkleFragmentInjector(): RecapInkleFragment

  @ContributesAndroidInjector(modules = [YarnChooserViewModelModule::class])
  internal abstract fun contributeYarnChooserFragmentInjector(): YarnPickerFragment

}

@Module
class InkleViewModelModule {

  @Provides
  internal fun provideViewModel(fragment: InkleListFragment, factory: ViewModelProvider.Factory)
      : InkleListViewModel {
    return ViewModelProvider(fragment, factory).get(InkleListViewModel::class.java)
  }
}

@Module
class SetUpInkleViewModelModule {

  @Provides
  internal fun provideViewModel(fragment: SetUpInkleFragment, factory: ViewModelProvider.Factory)
      : SetUpInkleViewModel {
    return ViewModelProvider(fragment, factory).get(SetUpInkleViewModel::class.java)
  }
}

@Module
class DrawInkleViewModelModule {

  @Provides
  internal fun provideViewModel(fragment: DrawInkleFragment, factory: ViewModelProvider.Factory)
      : DrawInkleViewModel {
    return ViewModelProvider(fragment, factory).get(DrawInkleViewModel::class.java)
  }
}

@Module
class RecapInkleViewModelModule {

  @Provides
  internal fun provideViewModel(fragment: RecapInkleFragment, factory: ViewModelProvider.Factory)
      : RecapInkleViewModel {
    return ViewModelProvider(fragment, factory).get(RecapInkleViewModel::class.java)
  }
}

@Module
class YarnChooserViewModelModule {

  @Provides
  internal fun provideViewModel(fragment: YarnPickerFragment, factory: ViewModelProvider.Factory)
      : YarnPickerViewModel {
    return ViewModelProvider(fragment, factory).get(YarnPickerViewModel::class.java)
  }
}

@Module
abstract class InkleViewModelBinderModule {

  @Binds
  @IntoMap
  @ViewModelKey(InkleListViewModel::class)
  internal abstract fun inkleListViewModel(viewModel: InkleListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(SetUpInkleViewModel::class)
  internal abstract fun setUpInkleViewModel(viewModel: SetUpInkleViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(DrawInkleViewModel::class)
  internal abstract fun drawInkleViewModel(viewModel: DrawInkleViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(RecapInkleViewModel::class)
  internal abstract fun recapInkleViewModel(viewModel: RecapInkleViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(YarnPickerViewModel::class)
  internal abstract fun yarnChooserViewModel(viewModel: YarnPickerViewModel): ViewModel

}