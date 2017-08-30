package com.mutlak.metis.wordmem.features.base

import rx.*
import rx.subscriptions.*

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the view that
 * can be accessed from the children classes by calling getView().
 */
open class BasePresenter<T : BaseView> : Presenter<T> {

  var view: T? = null
    private set
  private val mCompositeSubscription = CompositeSubscription()

  override fun attachView(view: T) {
    this.view = view
  }

  override fun detachView() {
    view = null
    if (!mCompositeSubscription.isUnsubscribed) {
      mCompositeSubscription.clear()
    }
  }

  private val isViewAttached: Boolean
    get() = view != null

  fun checkViewAttached() {
    if (!isViewAttached) throw MvpViewNotAttachedException()
  }

  fun addSubscription(subs: Subscription) {
    mCompositeSubscription.add(subs)
  }

  private class MvpViewNotAttachedException internal constructor() : RuntimeException(
      "Please call Presenter.attachView(BaseView) before" + " requesting data to the Presenter")

}

