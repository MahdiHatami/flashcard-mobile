package com.mutlak.metis.wordmem.features.base

import android.content.Context


/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
interface BaseView {
    fun showProgress()
    fun hideProgress()
    fun getContext() : Context
}
