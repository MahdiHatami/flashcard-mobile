package com.mutlak.metis.wordmem.features.landing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.R.color
import com.mutlak.metis.wordmem.extension.hide
import com.mutlak.metis.wordmem.extension.show
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.insertWord.NewWordActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.result.widget.CircleProgressView
import com.mutlak.metis.wordmem.features.review.ReviewActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import com.mutlak.metis.wordmem.util.NetworkUtil
import com.mutlak.metis.wordmem.util.ViewUtil
import com.victor.loading.book.BookLoading
import javax.inject.Inject


class LanActivity : BaseActivity(), LandingMvpView {

  companion object {
    const val REVIEW_TYPE = "review_type"
    const val DELAY_MILLIS: Long = 2000
    const val REVIEW_TYPE_BOOKMARK = 1
    const val REVIEW_TYPE_LEARNT = 2
    const val REVIEW_TYPE_NEW = 3
  }

  @Inject lateinit var mPresenter: LandingPresenter

  private lateinit var mDialog: MaterialDialog
  private var mAlertDialog: MaterialDialog? = null
  private var launchedActivity: Boolean = false

  @BindView(R.id.circle_progress_view) lateinit var mCircleProgressView: CircleProgressView
  @BindView(R.id.book_loading) lateinit var mBookLoading: BookLoading
  @BindView(R.id.loading_layout) lateinit var mLoadingLayout: LinearLayout
  @BindView(R.id.linear_content) lateinit var mContentLayout: LinearLayout
  @BindView(R.id.rel_review) lateinit var mReviewView: RelativeLayout
  @BindView(R.id.rel_learnt) lateinit var mLearntView: RelativeLayout
  @BindView(R.id.rel_bookmark) lateinit var mBookmarkedView: RelativeLayout
  @BindView(R.id.linear_take_quiz) lateinit var mTakeQuizView: LinearLayout
  @BindView(R.id.linear_plus) lateinit var mPlusView: LinearLayout
  @BindView(R.id.img_settings) lateinit var mSettings: ImageView

  @BindView(R.id.text_review) lateinit var mTotalReviewView: TextView
  @BindView(R.id.text_learnt) lateinit var mTotalLearntView: TextView
  @BindView(R.id.text_bookmark) lateinit var mTotalBookmarkedView: TextView

  @BindView(R.id.parent_view) lateinit var mParentView: ConstraintLayout

  @BindView(R.id.ripple_view) lateinit var mRippleView: View

  override val layout: Int
    get() = R.layout.activity_lan

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    setupStatusBar()
    setupView()

    mPresenter.checkIfNewWordsAvailable()
    mPresenter.getNewWordsCount()
    mPresenter.getLearntWordsCount()
    mPresenter.getBookmarkWordsCount()
    mPresenter.circleProgress();
  }

  override fun onResume() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && launchedActivity) {
      startRippleTransitionUnreveal();
      launchedActivity = false;
    }
    super.onResume()
  }

  private fun setupStatusBar() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      val window = window
      window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = ContextCompat.getColor(this, color.landing_header_center)
    }
  }


  private fun setupView() {
    mTakeQuizView.setOnClickListener { startActivity(Intent(this, QuizActivity::class.java)) }
    mSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    val intent = Intent(this, ReviewActivity::class.java)

    mReviewView.setOnClickListener { _ ->
      intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_NEW)
      startActivity(intent)
    }

    mLearntView.setOnClickListener {
      intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_LEARNT)
      startActivity(intent)
    }

    mBookmarkedView.setOnClickListener {
      intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_BOOKMARK)
      startActivity(intent)
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
    val snackbar = Snackbar.make(mParentView, R.string.internet_not_available,
        Snackbar.LENGTH_INDEFINITE)
    snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.yellow))
    snackbar.setAction(R.string.action_settings) { _ ->
      val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
      startActivity(intent)
    }
    val view = snackbar.view
    val tv = view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    tv.setTextColor(ContextCompat.getColor(this, R.color.accent))
    snackbar.show()
  }

  override fun showNewCount(count: Int) {
    mTotalReviewView.text = count.toString()
  }

  override fun showBookmarkCount(count: Int) {
    mTotalBookmarkedView.text = count.toString()
  }

  override fun showLearntCount(count: Int) {
    mTotalLearntView.text = count.toString()
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

  override fun onBackPressed() {
    redirectBack()
  }

  private fun redirectBack() {
    MaterialDialog.Builder(this).title(R.string.logout_title)
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

  override fun showCircleProgress(rate: Float) {
    mCircleProgressView.isTextEnabled = true
    mCircleProgressView.interpolator = AccelerateDecelerateInterpolator()
    mCircleProgressView.setProgressWithAnimation(rate, 1000)
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private fun startRippleTransitionUnreveal() {
    val animator: Animator = ViewAnimationUtils.createCircularReveal(
        mRippleView,
        (mPlusView.x + mPlusView.width / 2).toInt(),
        mPlusView.y.toInt(),
        ViewUtil.getViewRadius(mRippleView) * 2,
        (mPlusView.width / 2).toFloat());
    mRippleView.show()
    animator.interpolator = DecelerateInterpolator();
    animator.duration = 400;
    animator.addPauseListener(object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator?) {
        mContentLayout.animate().alpha(1f)
      }

      override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
        super.onAnimationEnd(animation, isReverse)
        mPlusView.visibility = View.VISIBLE;
        mRippleView.visibility = View.INVISIBLE;
      }
    })
    animator.start()
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private fun startRippleTransitionReveal() {
    mPlusView.hide()
    val animator: Animator = ViewAnimationUtils.createCircularReveal(mRippleView,
        (mPlusView.x + mPlusView.width / 2).toInt(), mPlusView.y.toInt(),
        (mPlusView.width / 2).toFloat(), ViewUtil.getViewRadius(mRippleView) * 2)
    mRippleView.show()
    animator.interpolator = AccelerateInterpolator();
    animator.duration = 500;
    animator.addPauseListener(object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator?) {
        mContentLayout.animate().alpha(0f)
      }

      override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
        super.onAnimationEnd(animation, isReverse)
        startActivity();
      }
    })
    animator.start()
  }

  fun startActivity() {
    val intent = Intent(this, NewWordActivity::class.java)
    ActivityCompat.startActivity(this, intent,
        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    launchedActivity = true;
  }

  @OnClick(R.id.linear_plus)
  fun plusOnClick(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      startRippleTransitionReveal();
    } else {
      startActivity();
    }
  }
}
