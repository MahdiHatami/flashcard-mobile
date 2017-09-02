package com.mutlak.metis.wordmem.features.result

import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import javax.inject.Inject


@ConfigPersistent
class ResultPresenter @Inject constructor(
    internal var repository: WordsRepositoryImpl) : BasePresenter<ResultView>() {


  override fun attachView(view: ResultView) {
    super.attachView(view)
  }

  fun getSetting(): Settings {
    return repository.settings
  }

}

