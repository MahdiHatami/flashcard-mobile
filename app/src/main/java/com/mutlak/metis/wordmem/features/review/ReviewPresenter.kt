package com.mutlak.metis.wordmem.features.review

import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.features.landing.LanActivity
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import javax.inject.Inject


@ConfigPersistent
class ReviewPresenter @Inject constructor(
    private val repo: WordsRepositoryImpl) : BasePresenter<ReviewView>() {

  private var words: List<Word>? = null

  override fun attachView(view: ReviewView) {
    super.attachView(view)
  }

  fun startCards(reviewType: Int) {
    val mReviewType = reviewType
    val mSettings = repo.settings
    val wordsList = repo.getReviewWords(reviewType, mSettings.reviewLimit).toMutableList()
    if (wordsList.isNotEmpty()) {
      wordsList.add(Word())
      words = wordsList
      view?.changeCurrentWord(wordsList.iterator().next())
      view?.initWordsPager(words!!)
      view?.changeCounterTitle("1/" + (words!!.size - 1))
    } else {
      prepareMessage(reviewType)
    }
  }

  private fun prepareMessage(reviewType: Int) {
    var title = 0
    var content = 0
    when (reviewType) {
      LanActivity.REVIEW_TYPE_NEW -> {
        title = R.string.not_enough_new_word_title
        content = R.string.not_enough_new_word_content
      }
      LanActivity.REVIEW_TYPE_BOOKMARK -> {
        title = R.string.not_enough_new_word_title
        content = R.string.not_enough_new_word_content
      }
      LanActivity.REVIEW_TYPE_LEARNT -> {
        title = R.string.not_enough_new_word_title
        content = R.string.not_enough_new_word_content
      }
    }
    view?.showAlert(title, content)
  }

  fun updateWord(word: Word) {
    repo.updateWord(word)
    view?.changeIconTextColor()
  }

  fun nextWord(position: Int) {
    if (position + 1 == words!!.size) {
      view?.hideToolbar()
    } else {
      val word = words!![position]
      view?.changeCounterTitle((position + 1).toString() + "/" + (words!!.size - 1))
      view?.changeCurrentWord(word)
      view?.changeSwitchesState()
      view?.changeIconTextColor()
      view?.showToolbar()
    }
  }
}
