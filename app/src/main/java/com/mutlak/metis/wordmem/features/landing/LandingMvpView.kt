package com.mutlak.metis.wordmem.features.landing;

import com.mutlak.metis.wordmem.features.base.BaseView

interface LandingMvpView : BaseView {

  fun showErrorMessage(code: Int)

  fun showOfflineMessage()

  fun showNewCount(count: Int)

  fun showBookmarkCount(count: Int)

  fun showLearntCount(count: Int)

  fun showBookLoading()

  fun hideBookLoading()

  fun showCircleProgress(rate: Float, totalLearnt: Int)

}

