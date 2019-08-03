package com.mutlak.metis.wordmem.features.landing

import android.annotation.SuppressLint
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.util.NetworkUtil
import com.mutlak.metis.wordmem.util.TimeUtil
import com.mutlak.metis.wordmem.util.rx.scheduler.SchedulerUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@ConfigPersistent
class LandingPresenter @Inject
constructor(private val repo: WordsRepositoryImpl,
    private val dataManager: DataManager) : BasePresenter<LandingMvpView>() {

  private val SIX_ANSWER = 2
  private val TEN_QUESTION = 1
  private val TEN_WORD_PER_REVIEW = 0
  private val FOUR_ANSWER = 0

  override fun attachView(view: LandingMvpView) {
    super.attachView(view)
  }


  fun checkIfNewWordsAvailable() {
    val settings = getSettings()

    val lastFetchDate = settings.lastFetchDate
    val date = TimeUtil.convertToDateObject(lastFetchDate)
    var timeAgo: String? = null
    if (date != null) {
      timeAgo = TimeUtil.getTimeAgo(date.time)
    }
    if (NetworkUtil.isNetworkAvailable(view!!.getContext())) {
      if (lastFetchDate == null) {
        loadWords()
      } else {
        if (timeAgo != null && timeAgo == TimeUtil.YESTERDAY) {
          loadWords(lastFetchDate)
        }
      }
    } else {
      view?.showOfflineMessage()
    }
  }

  fun getSettings(): Settings {
    var settings = repo.settings
    val userLanguage = Locale.getDefault().language
    if (settings.quizLimit == 0 || settings.reviewLimit == 0) {
      settings = Settings()
      settings.reviewLimit = SettingsActivity.reviewList[TEN_WORD_PER_REVIEW]
      settings.quizLimit = SettingsActivity.quizList[TEN_QUESTION]
      setupQuizType(settings, userLanguage)
      repo.updateSettings(settings)
    } else {
      if (settings.quizLimit == 0 || settings.reviewLimit == 0) {
        settings.reviewLimit = SettingsActivity.reviewList[TEN_WORD_PER_REVIEW]
        settings.quizLimit = SettingsActivity.quizList[TEN_QUESTION]
      }
    }
    if (settings.quizType == 0) setupQuizType(settings, userLanguage)
    return settings
  }


  @SuppressLint("CheckResult")
  fun loadWords(lastFetchDate: String = "") {
    view?.showBookLoading()
    dataManager.getWords(lastFetchDate)
        .compose(SchedulerUtils.ioToMain<List<Word>>())
        .subscribe({ words ->
          view?.hideBookLoading()
          updateWordsFetchedDate()
          if (words.isNotEmpty()) {
            repo.saveWords(words)
          }
          view?.showNewCount(repo.newWordsCount)
        }) { throwable ->
          view?.hideBookLoading()
          Timber.e(throwable)
        }
  }

  private fun updateWordsFetchedDate() {
    val settings = repo.settings
    settings.lastFetchDate = getCurrentDate()
    repo.updateSettings(settings)
  }


  private fun getCurrentDate(): String {
    return SimpleDateFormat(TimeUtil.API_DATE_FORMAT, Locale.getDefault()).format(
        Calendar.getInstance().time)
  }

  private fun setupQuizType(settings: Settings, userLanguage: String) {
    if (userLanguage == "tr") {
      settings.maxAnswers = SettingsActivity.optionList[SIX_ANSWER]
      settings.quizType = SettingsActivity.QUIZ_TYPE_WORD
    } else {
      settings.maxAnswers = SettingsActivity.optionList[FOUR_ANSWER]
      settings.quizType = SettingsActivity.QUIZ_TYPE_SENTENCE
    }
  }

  fun getNewWordsCount() {
    view?.showNewCount(repo.newWordsCount)
  }

  fun getBookmarkWordsCount() {
    view?.showBookmarkCount(repo.bookMarkWordsCount)
  }

  fun getLearntWordsCount() {
    view?.showLearntCount(repo.learntWordsCount)
  }

  fun circleProgress() {
    val words = repo.allWords
    val learned = words.filter { it.learnt && !it.ignored }.count()
    var rate = 0F
    if (learned > 0)
      rate = (learned * 100 / words.size).toFloat()
    view?.showCircleProgress(rate, learned)
  }
}

