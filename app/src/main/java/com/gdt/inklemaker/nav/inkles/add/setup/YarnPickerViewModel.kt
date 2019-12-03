package com.gdt.inklemaker.nav.inkles.add.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.nav.yarns.add.YarnRepository
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import javax.inject.Inject

class YarnPickerViewModel @Inject constructor(repository: YarnRepository) : ViewModel() {

  private var disposable: Disposable = Disposables.empty()

  private val _yarns: LiveData<List<Yarn>> = repository.getAll()

  val yarns: LiveData<List<Yarn>> = Transformations.map(_yarns) { list -> list.sortedBy { it.name } }

  val isListEmpty: LiveData<Boolean> = Transformations.map(_yarns) { it.isEmpty() }

  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }
}

