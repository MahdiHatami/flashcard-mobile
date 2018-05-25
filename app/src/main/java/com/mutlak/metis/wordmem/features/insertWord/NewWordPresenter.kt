package com.mutlak.metis.wordmem.features.insertWord

import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.util.rx.scheduler.SchedulerUtils
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@ConfigPersistent
class NewWordPresenter @Inject
constructor(private val mRepo: WordsRepositoryImpl,
    private val mDataManager: DataManager) : BasePresenter<NewWordView>() {

  companion object {
    const val exist = "1001"
    const val added = "1002"
    const val notSaved = "1003"
    const val wordEmpty = "1004"
  }

  private fun sendWord(word: Word, file: File?) {
    // check if word exsits in db
    mDataManager.saveWord(word)
    view?.showProgress()
    if (file != null) {
      val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
      val body = MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
      mDataManager.sendWord(body, word)
          .compose<ResponseBody>(SchedulerUtils.ioToMain<ResponseBody>())
          .subscribe({
            view?.hideProgress()

            when (it.string()) {
              added -> {
                view?.showAlert(R.string.new_word, R.string.word_added_successfully,
                    SweetAlertDialog.SUCCESS_TYPE)
                view?.cleanForm()
              }
              exist -> {
                view?.showAlert(R.string.new_word, R.string.word_already_has_been_added,
                    SweetAlertDialog.ERROR_TYPE)
              }
              notSaved -> {
                view?.showAlert(R.string.new_word,
                    R.string.word_could_not_be_added,
                    SweetAlertDialog.ERROR_TYPE)
              }
              wordEmpty -> {
                view?.showAlert(R.string.new_word,
                    R.string.internet_server_not_found_content,
                    SweetAlertDialog.ERROR_TYPE)
              }
            }
          })
          {
            Timber.e(it, "error while uploading")
            view?.hideProgress()
            view?.showError(it)
          }

    } else {
      mDataManager.sendWord(word)
          .compose<ResponseBody>(SchedulerUtils.ioToMain<ResponseBody>())
          .subscribe({
            view?.hideProgress()

            when (it.string()) {
              added -> {
                view?.showAlert(R.string.new_word, R.string.word_added_successfully,
                    SweetAlertDialog.SUCCESS_TYPE)
                view?.cleanForm()
              }
              exist -> {
                view?.showAlert(R.string.new_word, R.string.word_already_has_been_added,
                    SweetAlertDialog.ERROR_TYPE)
                view?.cleanForm()
              }
              notSaved -> {
                view?.showAlert(R.string.new_word,
                    R.string.word_could_not_be_added,
                    SweetAlertDialog.ERROR_TYPE)
              }
              wordEmpty -> {
                view?.showAlert(R.string.new_word,
                    R.string.internet_server_not_found_content,
                    SweetAlertDialog.ERROR_TYPE)
              }
            }
          })
          {
            Timber.e(it, "error while uploading")
            view?.hideProgress()
            view?.showError(it)
          }
    }
  }

  fun saveWord(word: Word, file: File?) {
    val isExist = mRepo.isWordExist(word)
    if (isExist) {
      view?.showAlert(R.string.new_word, R.string.word_already_has_been_added,
          SweetAlertDialog.ERROR_TYPE)
    } else {
      sendWord(word, file)
    }
  }
}
