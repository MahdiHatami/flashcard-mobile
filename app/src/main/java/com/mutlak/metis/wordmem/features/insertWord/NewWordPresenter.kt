package com.mutlak.metis.wordmem.features.insertWord

import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.util.rx.scheduler.SchedulerUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject


@ConfigPersistent
class NewWordPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<NewWordView>() {

  override fun attachView(view: NewWordView) {
    super.attachView(view)
  }

  fun sendWord(word: Word) {
    // check if word exsits in db
    mDataManager.saveWord(word)
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
