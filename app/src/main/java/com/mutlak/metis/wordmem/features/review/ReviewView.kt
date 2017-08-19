package com.mutlak.metis.wordmem.features.review

import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BaseView


interface ReviewView : BaseView {

    fun initWordsPager(list: List<Word>)

    fun changeIconTextColor()

    fun changeCounterTitle(s: String)

    fun changeCurrentWord(word: Word)

    fun changeSwitchesState()

    fun hideLoading()

    fun showAlert(title: Int, content: Int)

    fun showLastPageView()

    fun hideToolbar()

    fun showToolbar()
}
