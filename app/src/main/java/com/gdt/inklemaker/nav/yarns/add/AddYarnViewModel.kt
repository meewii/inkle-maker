package com.gdt.inklemaker.nav.yarns.add

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.core.database.YarnSize
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import meewii.core.ViewStatus
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class AddYarnViewModel @Inject constructor(private val repository: YarnRepository) : ViewModel() {

  private var disposable: Disposable = Disposables.empty()

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  fun resetViewStatus() {
    _viewStatus.value = ViewStatus.Idle
  }

  private val _yarn: MutableLiveData<Yarn> = MutableLiveData()
  val yarn: LiveData<Yarn> = _yarn

  val name: MutableLiveData<String> = MutableLiveData()
  val nameError: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = null }

  private val _size: MutableLiveData<YarnSize> = MutableLiveData<YarnSize>().apply { value = YarnSize.M }
  val size: LiveData<YarnSize> = _size

  private val _color: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = Color.BLACK }
  private val _colorHex: MutableLiveData<String> = MutableLiveData()
  val color: LiveData<Int> = _color
  val colorHex: LiveData<String> = _colorHex
  val colorError: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = null }

  fun setSize(size: String) {
    _size.value = YarnSize.valueOf(size)
  }

  fun setColor(color: Int, hexCode: String) {
    _color.value = color
    _colorHex.value = "#$hexCode"
    if (hexCode.isBlank()) {
      colorError.value = R.string.form_error_empty
    } else {
      colorError.value = null
    }
  }

  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }

  fun onAddYarnDone() {
    val newYarn = evaluateForm()
    if (newYarn != null) {
      disposable = repository.createYarn(newYarn)
        .subscribe({
          Timber.i("Yarn created with success")
          _viewStatus.value = ViewStatus.Success()
        }, {
          Timber.e("Error while creating Yarn", it)
          _viewStatus.value = ViewStatus.Error(it.message)
        })
    } else {
      _viewStatus.value = ViewStatus.Error()
    }
  }

  private fun evaluateForm(): Yarn? {
    var hasError = false

    val name = this.name.value
    if (name.isNullOrBlank()) {
      nameError.value = R.string.form_error_empty
      hasError = true
    } else {
      nameError.value = null
    }

    val color = this._colorHex.value
    if (color.isNullOrBlank()) {
      colorError.value = R.string.form_error_empty
      hasError = true
    } else {
      nameError.value = null
    }

    val size = this._size.value ?: YarnSize.M

    return if (hasError.not()) {
      Yarn(
        id = UUID.randomUUID().toString(),
        name = name!!,
        color = color!!,
        size = size
      )
    } else null
  }

}