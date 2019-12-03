package com.gdt.inklemaker.nav.inkles.add.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.nav.inkles.add.InkleRepository
import com.gdt.inklemaker.nav.yarns.add.YarnRepository
import io.reactivex.disposables.Disposables
import meewii.core.ViewStatus
import meewii.core.toIntOrValue
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class SetUpInkleViewModel @Inject constructor(
  private val inkleRepository: InkleRepository,
  yarnRepository: YarnRepository
) : ViewModel() {

  private var disposable = Disposables.disposed()

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _yarns: LiveData<List<Yarn>> = yarnRepository.getAll()
  val yarnCount: LiveData<Int> = Transformations.map(_yarns) { it.size }

  fun resetViewStatus() {
    _viewStatus.value = ViewStatus.Idle
  }

  val name: MutableLiveData<String> = MutableLiveData()
  val nameError: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = null }

  val length: MutableLiveData<String> = MutableLiveData()
  val lengthError: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = null }

  val totalThreadCount: MutableLiveData<String> = MutableLiveData()
  private val _upperThreadCount: MutableLiveData<Int> = MutableLiveData()
  private val _lowerThreadCount: MutableLiveData<Int> = MutableLiveData()
  val threadCountError: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = null }

  val splitThreadCount: LiveData<Pair<Int, Int>> = Transformations.map(totalThreadCount) {
    var upperThreads = -1
    var lowerThreads = -1
    if (!it.isNullOrBlank()) {
      val totalThreads = it.toIntOrValue(-1)
      if (totalThreads < 0) {
        _viewStatus.value = ViewStatus.Error("Invalid thread number")
      } else {
        lowerThreads = totalThreads / 2
        upperThreads = totalThreads - lowerThreads
      }
    }

    if (upperThreads > 0) _upperThreadCount.value = upperThreads
    if (lowerThreads > 0) _lowerThreadCount.value = lowerThreads

    Pair(upperThreads, lowerThreads)
  }

  val yarnList: MutableLiveData<List<Yarn>> = MutableLiveData()
  val yarnError: MutableLiveData<Int> = MutableLiveData()
  val weft: MutableLiveData<Yarn> = MutableLiveData()

  fun onSetUpDone() {
    val setUpInkle = evaluateForm()
    if (setUpInkle != null) {
      disposable = inkleRepository.createInkle(setUpInkle)
        .subscribe({
          _viewStatus.value = ViewStatus.Success(setUpInkle.id)
          Timber.v("Inkle created")
        }, {
          Timber.e("Error while creating Inkle", it)
        })
    } else {
      _viewStatus.value = ViewStatus.Error()
    }
  }

  private fun evaluateForm(): Inkle? {
    var hasError = false

    val name = this.name.value
    if (name.isNullOrBlank()) {
      nameError.value = R.string.form_error_empty
      hasError = true
    } else {
      nameError.value = null
    }

    val upperThreads = _upperThreadCount.value
    val lowerThreads = _lowerThreadCount.value
    if ((upperThreads == null || upperThreads < 0) || (lowerThreads == null || lowerThreads < 0)) {
      threadCountError.value = R.string.form_error_empty
      hasError = true
    } else if (upperThreads + lowerThreads > 100) {
      threadCountError.value = R.string.set_up_thread_count_error
      hasError = true
    }

    val length = length.value?.toIntOrValue(0) ?: 0
    if (length > 5000) {
      lengthError.value = R.string.set_up_length_error
      hasError = true
    }

    val yarns = yarnList.value as? ArrayList<Yarn>
    val weft = this.weft.value
    if (yarns == null || yarns.isEmpty()) {
      yarnError.value = R.string.form_error_empty
      hasError = true
    } else {
      yarnError.value = null
      if (weft != null && yarns.none { it.id == weft.id }) yarns.add(weft)
    }

    return if (hasError.not()) {
      // assign default yarn colors
      val upperWarpYarnIds = arrayListOf<String>()
      val lowerWarpYarnIds = arrayListOf<String>()
      val defaultYarn = yarns!!.first().id
      for (i in 0..upperThreads!!) {
        upperWarpYarnIds.add(defaultYarn)
      }
      for (i in 0..lowerThreads!!) {
        lowerWarpYarnIds.add(defaultYarn)
      }

      Inkle(
        id = UUID.randomUUID().toString(),
        name = name!!,
        upperWarpYarnIds = upperWarpYarnIds,
        lowerWarpYarnIds = lowerWarpYarnIds,
        lengthCm = length,
        yarns = yarns,
        weftYarnId = weft?.id,
        createdAt = System.currentTimeMillis()
      )
    } else null
  }

  fun updateYarnList(selectedYarns: List<Yarn>) {
    yarnList.value = selectedYarns
    if (selectedYarns.isEmpty()) {
      yarnError.value = R.string.form_error_empty
    } else {
      yarnError.value = null
    }
  }

  fun updateWeft(selectedWeft: Yarn) {
    weft.value = selectedWeft
  }

  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }
}
