package com.mutlak.metis.wordmem.features.insertWord

import com.mutlak.metis.wordmem.data.*
import com.mutlak.metis.wordmem.data.model.*
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.injection.*
import com.mutlak.metis.wordmem.util.rx.scheduler.*
import okhttp3.*
import java.io.*
import javax.inject.*


@ConfigPersistent
class NewWordPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<NewWordView>() {

  override fun attachView(view: NewWordView) {
    super.attachView(view)
  }

  fun sendWord(word: Word) {
    // check if word exsits in db
    view?.showProgress()
    val file: File? = null
    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
    val body = MultipartBody.Part.createFormData("file", file!!.name, requestFile)
    mDataManager.sendWord(body, word)
        .compose<ResponseBody>(SchedulerUtils.ioToMain<ResponseBody>())
        .subscribe({
          if (it.string() == "") {

          } else {

          }
        })
        {
          view?.hideProgress()
          view?.showError(it)
        }
  }
}
