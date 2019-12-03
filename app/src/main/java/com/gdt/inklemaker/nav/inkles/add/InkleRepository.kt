package com.gdt.inklemaker.nav.inkles.add

import android.view.View
import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.core.database.InkleDao
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InkleRepository @Inject constructor(private val inkleDao: InkleDao,
                                          private val previewStorageRepository: PreviewStorageRepository) {

  private var disposable = Disposables.disposed()

  fun createInkle(inkle: Inkle): Single<Unit> {
    inkle.yarns.map { it.isSelected = false }

    return inkleDao.insert(inkle)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun getInkle(inkleId: String): Single<Inkle> {
    return inkleDao.getRow(inkleId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun updateInkle(inkle: Inkle): Single<Unit> {
    return inkleDao.update(inkle)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  /**
   * Returns all persisted inkles
   */
  fun getAll(): Flowable<List<Inkle>> = inkleDao.getAll()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  /**
   * Returns all persisted inkles as LiveData
   */
  fun getAllLiveData() = inkleDao.getAllLiveData()

  /**
   * Deletes inkle of given ID permanently
   */
  fun delete(inkleId: String): Single<Int> {
    return inkleDao.delete(inkleId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun saveToStorage(view: View, inkleId: String, width: Float, height: Float): Single<PreviewStorageRepository.Status> {
    return Single.fromCallable { previewStorageRepository.createBitmapFromView(view, width, height) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { bitmap ->
        if (bitmap.value == null) {
          PreviewStorageRepository.Status.Error("Unknown error occurred while saving picture")
        } else {
          previewStorageRepository.write(inkleId, bitmap.value)
        }
      }
      .onErrorReturn { PreviewStorageRepository.Status.Error("Unknown error occurred while saving picture") }
      .map {
        if (it is PreviewStorageRepository.Status.SuccessWriting) {
          updateInkleWithPreview(inkleId, it.path)
        }
        it
      }
  }

  private fun updateInkleWithPreview(inkleId: String, path: String?) {
    disposable = getInkle(inkleId)
      .flatMap {
        it.savedImagePath = path
        updateInkle(it)
      }
      .subscribe({
        Timber.d("Inkle updated")
      }, {
        Timber.d("Inkle updated error", it)
      })
  }
}