package com.mutlak.metis.wordmem.features.insertWord

import com.mutlak.metis.wordmem.features.base.*


interface NewWordView : BaseView {

  fun showError(throwable: Throwable)
}
