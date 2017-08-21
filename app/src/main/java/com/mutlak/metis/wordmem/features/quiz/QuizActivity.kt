package com.mutlak.metis.wordmem.features.quiz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Answer
import com.mutlak.metis.wordmem.data.model.ExamSession
import com.mutlak.metis.wordmem.data.model.Question
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.quiz.widgets.CustomViewPager
import com.mutlak.metis.wordmem.features.quiz.widgets.CustomViewPager.SwipeDirection
import com.mutlak.metis.wordmem.features.quiz.widgets.ExamPagerAdapter
import com.mutlak.metis.wordmem.features.result.ResultActivity
import java.util.Locale
import javax.inject.Inject

class QuizActivity : BaseActivity(), QuizView {

  companion object {
    val EXAM_SESSION = "exam_session"
  }


  @Inject lateinit var mPresenter: QuizPresenter

  private var session: ExamSession? = null

  @BindView(R.id.test_view_pager) lateinit var mViewPager: CustomViewPager
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.toolbar_counter) lateinit var mToolbarCounter: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.statusBarColor = ContextCompat.getColor(this, R.color.red_dark)
    }

    setupToolbar()

    mPresenter.setupTest()
  }

  override val layout: Int get() = R.layout.activity_quiz

  fun nextQuestion(preQuestion: Question, answer: Answer, userAnswer: Boolean) {
    session!!.questions!![mViewPager.currentItem].isUserAnswerWasCorrect = preQuestion.isUserAnswerWasCorrect
    session!!.questions!![mViewPager.currentItem].userResponse = answer.word
    if (mViewPager.currentItem.plus(1) == session!!.questions!!.size) {
      Handler().postDelayed({ showResultPage(session!!) }, 500)
    } else {
      if (userAnswer) {
        Handler().postDelayed({
          val currentIndex = mViewPager.currentItem.plus(2)
          val total = session!!.questions!!.size
          mToolbarCounter.text = getString(R.string.toolbar_counter_title, currentIndex, total)
          mViewPager.currentItem = mViewPager.currentItem + 1
        }, 700)
      } else {
        Handler().postDelayed({
          val currentIndex = mViewPager.currentItem.plus(2)
          val total = session!!.questions!!.size
          mToolbarCounter.text = getString(R.string.toolbar_counter_title, currentIndex, total)
          mViewPager.currentItem = mViewPager.currentItem.plus(1)
        }, 1000)
      }
    }
  }

  private fun showResultPage(session: ExamSession) {
    val intent = Intent(this, ResultActivity::class.java)
    intent.putExtra(EXAM_SESSION, Gson().toJson(session))
    startActivity(intent)
  }

  override fun getContext(): Context {
    return this
  }

  override fun redirectBack() {
    MaterialDialog.Builder(this@QuizActivity).title(R.string.not_enough_new_word_title)
        .content(R.string.quiz_not_enough_word_content)
        .cancelable(false)
        .positiveText(R.string.ok)
        .onPositive { dialog, _ ->
          dialog.dismiss()
          finish()
          redirectToLanding()
        }
        .onNegative { dialog, _ -> dialog.dismiss() }
        .show()
  }

  override fun initQuestionPager(session: ExamSession) {
    this.session = session
    val adapter = ExamPagerAdapter(this.session!!, supportFragmentManager)
    mViewPager.adapter = adapter
    mViewPager.setAllowedSwipeDirection(SwipeDirection.none)
    mViewPager.setScrollDurationFactor(2.0)
    mToolbarCounter.text = String.format(Locale.US, "1/%d", this.session!!.questions!!.size)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        askForLeave()
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    askForLeave()
  }

  private fun askForLeave() {
    MaterialDialog.Builder(this@QuizActivity).title(R.string.quiz_cancel_dialog_title)
        .content(R.string.quiz_cancel_dialog_content)
        .positiveText(R.string.yes)
        .negativeText(R.string.cancel)
        .onPositive { dialog, _ ->
          dialog.dismiss()
          finish()
          redirectToLanding()
        }
        .onNegative { dialog, _ -> dialog.dismiss() }
        .show()
  }

  private fun redirectToLanding() {
    startActivity(Intent(this@QuizActivity, LandingActivity::class.java))
  }

  private fun setupToolbar() {
    setSupportActionBar(toolbar)
    if (supportActionBar != null) {
      supportActionBar!!.setDisplayShowHomeEnabled(true)
      supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      supportActionBar!!.setDisplayShowTitleEnabled(false)
      supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_clear)
    }
  }

  override fun onDestroy() {
    mPresenter.detachView()
    super.onDestroy()
  }
}
