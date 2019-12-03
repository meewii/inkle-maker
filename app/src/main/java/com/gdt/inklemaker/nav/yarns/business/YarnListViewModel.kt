package com.gdt.inklemaker.nav.yarns.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.nav.yarns.add.YarnRepository
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import javax.inject.Inject

class YarnListViewModel @Inject constructor(private val repository: YarnRepository) : ViewModel() {

  private var disposable: Disposable = Disposables.empty()

  val yarns: LiveData<List<Yarn>> = repository.getAll()

  fun deleteYarn(yarn: Yarn) {
    disposable = repository.delete(yarn.id)
      .subscribe({
        Timber.i("Yarn deleted with success: $it rows deleted")
      }, {
        Timber.e("Error while deleting Yarn", it)
      })
  }

  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }
}
