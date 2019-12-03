package com.gdt.inklemaker.nav.yarns.add

import androidx.lifecycle.LiveData
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.core.database.YarnDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YarnRepository @Inject constructor(private val yarnDao: YarnDao) {

  fun createYarn(yarn: Yarn): Single<Unit> {
    return yarnDao.insert(yarn)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun getAll(): LiveData<List<Yarn>> = yarnDao.getAll()

  fun delete(yarnId: String): Single<Int> {
    return yarnDao.delete(yarnId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

}
