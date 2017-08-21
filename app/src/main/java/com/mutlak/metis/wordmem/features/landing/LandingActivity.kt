package com.mutlak.metis.wordmem.features.landing

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.review.ReviewActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import com.mutlak.metis.wordmem.util.NetworkUtil
import com.victor.loading.book.BookLoading
import javax.inject.Inject

class LandingActivity : BaseActivity(), LandingMvpView {

  private lateinit var mRelativeBookmark: RelativeLayout
  private lateinit var mRelativeLearnt: RelativeLayout
  private lateinit var mRelativeNew: RelativeLayout
  private lateinit var mTextNewCount: TextView
  private lateinit var mTextBookmarkCount: TextView
  private lateinit var mTextLearntCount: TextView

  private lateinit var mDialog: MaterialDialog
  private var mAlertDialog: MaterialDialog? = null

  @Inject lateinit var mPresenter: LandingPresenter

  @BindView(R.id.review_card) lateinit var mReviewCard: CardView
  @BindView(R.id.word_card_test) lateinit var mQuizCard: CardView
  @BindView(R.id.settings_card) lateinit var mSettingsCard: CardView
  @BindView(R.id.content_layout) lateinit var mContentLayout: LinearLayout
  @BindView(R.id.loading_layout) lateinit var mLoadingLayout: LinearLayout
  @BindView(R.id.book_loading) lateinit var mBookLoading: BookLoading
  @BindView(R.id.container) lateinit var parentView: CoordinatorLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    mPresenter.checkIfNewWordsAvailable()

    setupReviewDialog()

    setupMenu()
  }

  override val layout: Int
    get() = R.layout.activity_landing

  companion object {
    const val REVIEW_TYPE = "review_type"
    const val DELAY_MILLIS: Long = 2000
    const val REVIEW_TYPE_BOOKMARK = 1
    const val REVIEW_TYPE_LEARNT = 2
    const val REVIEW_TYPE_NEW = 3
  }

  private fun setupMenu() {
    mReviewCard.background = ContextCompat.getDrawable(this, R.drawable.answer_card_correct_fill)
    mQuizCard.background = ContextCompat.getDrawable(this, R.drawable.answer_card_wrong_fill)
    mSettingsCard.background = ContextCompat.getDrawable(this,
        R.drawable.landing_settings_card_fill)

    mQuizCard.setOnClickListener { _ ->
      val i = Intent(this@LandingActivity, QuizActivity::class.java)
      startActivity(i)
    }
    mSettingsCard.setOnClickListener { _ ->
      val i = Intent(this@LandingActivity, SettingsActivity::class.java)
      startActivity(i)
    }
  }

  private fun setupReviewDialog() {
    mReviewCard.setOnClickListener { _ ->
      mDialog = MaterialDialog.Builder(this).customView(R.layout.landing_menu_custom_view, false)
          .show()

      mRelativeBookmark = mDialog.findViewById(R.id.dialog_bookmark) as RelativeLayout
      mRelativeLearnt = mDialog.findViewById(R.id.dialog_learnt) as RelativeLayout
      mRelativeNew = mDialog.findViewById(R.id.dialog_new) as RelativeLayout

      mTextNewCount = mDialog.findViewById(R.id.review_new_count) as TextView
      mTextBookmarkCount = mDialog.findViewById(R.id.review_bookmark_count) as TextView
      mTextLearntCount = mDialog.findViewById(R.id.review_learnt_count) as TextView

      mPresenter.getNewWordsCount()
      mPresenter.getLearntWordsCount()
      mPresenter.getBookmarkWordsCount()


      val intent = Intent(this@LandingActivity, ReviewActivity::class.java)
      mRelativeBookmark.setOnClickListener {
        intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_BOOKMARK)
        startActivity(intent)
        mDialog.dismiss()
      }
      mRelativeLearnt.setOnClickListener {
        mDialog.dismiss()
        intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_LEARNT)
        startActivity(intent)
      }
      mRelativeNew.setOnClickListener {
        mDialog.dismiss()
        intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_NEW)
        startActivity(intent)
      }
    }
  }

  private fun showAlert(title: Int, content: Int) {
    if (mAlertDialog == null) {
      mAlertDialog = MaterialDialog.Builder(this).title(title)
          .content(content)
          .cancelable(false)
          .positiveText(R.string.ok)
          .show()
    } else {
      mAlertDialog?.setContent(content)
      mAlertDialog?.show()
    }
  }

  override fun showErrorMessage(code: Int) {
    when (code) {
      NetworkUtil.HTTP_NOT_FOUND -> showAlert(R.string.internet_connection_error_title,
          R.string.internet_not_found_content)
      NetworkUtil.HTTP_SERVER_ERROR -> showAlert(R.string.internet_connection_error_title,
          R.string.internet_server_not_found_content)
    }

  }

  override fun showOfflineMessage() {
    val snackbar = Snackbar.make(parentView, R.string.internet_not_available,
        Snackbar.LENGTH_INDEFINITE)
    snackbar.setActionTextColor(ContextCompat.getColor(this@LandingActivity, R.color.yellow))
    snackbar.setAction(R.string.action_settings) { _ ->
      val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
      startActivity(intent)
    }
    val view = snackbar.view
    val tv = view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    tv.setTextColor(ContextCompat.getColor(this@LandingActivity, R.color.accent))
    snackbar.show()
  }

  override fun showNewCount(count: Int) {
    if (count > 0) {
      mTextNewCount.text = count.toString()
    } else {
      mTextNewCount.visibility = View.GONE
    }
  }

  override fun showBookmarkCount(count: Int) {
    if (count > 0) {
      mTextBookmarkCount.text = count.toString()
    } else {
      mTextBookmarkCount.visibility = View.GONE
    }
  }

  override fun showLearntCount(count: Int) {
    if (count > 0) {
      mTextLearntCount.text = count.toString()
    } else {
      mTextLearntCount.visibility = View.GONE
    }
  }


  override fun showBookLoading() {
    mLoadingLayout.visibility = View.VISIBLE
    changeStatusBarColor(R.color.primary_dark)
    mContentLayout.visibility = View.GONE
    mBookLoading.start()
  }

  override fun hideBookLoading() {
    Handler().postDelayed({
      mLoadingLayout.visibility = View.GONE
      mContentLayout.visibility = View.VISIBLE
      changeStatusBarColor(R.color.primary_dark)
    }, DELAY_MILLIS)
  }

  private fun changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.statusBarColor = ContextCompat.getColor(this, color)
    }
  }

  override fun onBackPressed() {
    redirectBack()
  }

  private fun redirectBack() {
    MaterialDialog.Builder(this@LandingActivity).title(R.string.logout_title)
        .content(R.string.logout_content)
        .positiveText(R.string.yes)
        .negativeText(R.string.cancel)
        .onPositive { dialog, _ ->
          dialog.dismiss()
          finishAffinity()
        }
        .onNegative { dialog, _ -> dialog.dismiss() }
        .show()
  }

  override fun getContext(): Context {
    return this
  }
}
