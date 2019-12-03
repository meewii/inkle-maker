package com.gdt.inklemaker.nav.inkles.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.nav.inkles.add.InkleRepository
import com.gdt.inklemaker.nav.inkles.add.PreviewStorageRepository
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import javax.inject.Inject

class InkleListViewModel @Inject constructor(private val repository: InkleRepository,
                                             private val previewStorageRepository: PreviewStorageRepository) : ViewModel() {

  private val _inkles: MutableLiveData<List<Inkle>> = MutableLiveData()
  val inkles: LiveData<List<Inkle>> = _inkles

  private var deleteDisposable: Disposable = Disposables.empty()
  private var getAllDisposable: Disposable = Disposables.empty()

  fun subscribeToInkles() {
    getAllDisposable = repository.getAll()
      .map { inkles ->
        inkles.map { inkle ->
          when (val status = previewStorageRepository.read(inkle.savedImagePath)) {
            is PreviewStorageRepository.Status.SuccessReading -> inkle.savedImage = status.bitmap
            else -> { /* TODO retry to recreate image */}
          }
        }
        inkles
      }
      .subscribe({
        _inkles.value = it
      }, {
        Timber.e("Error when get all inkles", it)
      })
  }

  /**
   * Deletes inkle permanently
   */
  fun delete(inkleId: String) {
    deleteDisposable = repository.delete(inkleId)
      .subscribe({
        Timber.d("$inkleId deleted: $it")
      }, {
        Timber.e("Error when deleting $inkleId", it)
      })
  }

  override fun onCleared() {
    super.onCleared()
    deleteDisposable.dispose()
    getAllDisposable.dispose()
  }
}
