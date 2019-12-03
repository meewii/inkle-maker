package com.gdt.inklemaker.nav.inkles.add.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.nav.inkles.add.InkleRepository
import io.reactivex.disposables.Disposables
import meewii.core.ViewStatus
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class DrawInkleViewModel @Inject constructor(private val repository: InkleRepository) : ViewModel() {

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _wipInkle: MutableLiveData<Inkle> = MutableLiveData()
  val wipInkle: LiveData<Inkle> = _wipInkle

  private var getDisposable = Disposables.disposed()
  private var updateDisposable = Disposables.disposed()

  fun getInkle(inkleId: String) {
    getDisposable = repository.getInkle(inkleId)
      .subscribe({
        _wipInkle.value = it
      },{
        Timber.e("Error while getting Inkle #$inkleId", it)
      })
  }

  fun saveDrawing(inkleId: String, upperYarnIds: ArrayList<String>,
                  lowerYarnIds: ArrayList<String>, weftYarnId: String?) {

    updateDisposable = repository.getInkle(inkleId)
        .map {
          it.apply {
            updatedAt = System.currentTimeMillis()
            upperWarpYarnIds = upperYarnIds
            lowerWarpYarnIds = lowerYarnIds
            this.weftYarnId = weftYarnId
          }
          it
        }
        .flatMap { repository.updateInkle(it) }
        .subscribe({
          _viewStatus.value = ViewStatus.Success(inkleId)
        },{
          _viewStatus.value = ViewStatus.Error(it.message)
          Timber.e("Error while getting Inkle #$inkleId", it)
        })
  }

  override fun onCleared() {
    super.onCleared()
    getDisposable.dispose()
    updateDisposable.dispose()
  }

}
