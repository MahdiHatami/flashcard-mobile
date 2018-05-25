package com.mutlak.metis.wordmem.features.insertWord

import com.mutlak.metis.wordmem.features.base.BaseView


interface NewWordView : BaseView {

  fun showError(throwable: Throwable)
  fun showAlert(title: Int, message: Int, type: Int)
  fun cleanForm()
}
