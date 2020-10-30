package com.mutlak.metis.wordmem.features.landing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.mutlak.metis.wordmem.R
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
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject


class LandingActivity : BaseActivity(), LandingMvpView {

  companion object {
    const val REVIEW_TYPE = "review_type"
    const val DELAY_MILLIS: Long = 2000
    const val REVIEW_TYPE_BOOKMARK = 1
    const val REVIEW_TYPE_LEARNT = 2
    const val REVIEW_TYPE_NEW = 3
  }

  @Inject lateinit var mPresenter: LandingPresenter

  private var mAlertDialog: MaterialDialog? = null
  private var launchedActivity: Boolean = false

  @BindView(R.id.circle_progress_view) lateinit var mCircleProgressView: CircleProgressView
  @BindView(R.id.progress_horizontal) lateinit var mHorizontalProgressView: ProgressBar
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
  @BindView(R.id.text_total_learnt_words) lateinit var mTotalLearntWordsView: TextView

  @BindView(R.id.parent_view) lateinit var mParentView: ConstraintLayout

  @BindView(R.id.ripple_view) lateinit var mRippleView: View

  override val layout: Int
    get() = R.layout.activity_landing

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
    mPresenter.circleProgress()
  }

  override fun onResume() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && launchedActivity) {
      startRippleTransitionUnreveal()
      launchedActivity = false
    }
    super.onResume()
  }

  private fun setupStatusBar() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      val window = window
      window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      changeStatusBarColor(R.color.landing_header_end)
    }
  }


  @SuppressLint("CheckResult")
  private fun setupView() {
    RxView.clicks(mPlusView).debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
          if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            startRippleTransitionReveal()
          } else {
            startActivity()
          }
        }

    RxView.clicks(mTakeQuizView)
        .debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
          startActivity(Intent(this, QuizActivity::class.java))
        }


    RxView.clicks(mSettings)
        .debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
          startActivity(Intent(this, SettingsActivity::class.java))
        }

    val intent = Intent(this, ReviewActivity::class.java)

    RxView.clicks(mReviewView)
        .debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
          intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_NEW)
          startActivity(intent)
        }

    RxView.clicks(mLearntView)
        .debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
          intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_LEARNT)
          startActivity(intent)
        }

    RxView.clicks(mBookmarkedView)
        .debounce(500, MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
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
    snackbar.setAction(R.string.action_settings) {
      val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
      startActivity(intent)
    }
    val view = snackbar.view
    val tv = view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
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
    changeStatusBarColor(R.color.landing_header_start)
    mParentView.visibility = View.GONE
    mBookLoading.start()
  }

  override fun hideBookLoading() {
    Handler().postDelayed({
      mLoadingLayout.visibility = View.GONE
      mParentView.visibility = View.VISIBLE
      changeStatusBarColor(R.color.landing_header_start)
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

  override fun showCircleProgress(rate: Float, totalLearnt: Int) {
    mCircleProgressView.isTextEnabled = true
    mCircleProgressView.interpolator = AccelerateDecelerateInterpolator()
    mCircleProgressView.setProgressWithAnimation(rate, 1000)

    mHorizontalProgressView.progress = rate.toInt()
    mTotalLearntWordsView.text = getString(R.string.landing_header_learnt_words, totalLearnt)
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private fun startRippleTransitionUnreveal() {
    val animator: Animator = ViewAnimationUtils.createCircularReveal(
        mRippleView,
        (mPlusView.x + mPlusView.width / 2).toInt(),
        mPlusView.y.toInt(),
        ViewUtil.getViewRadius(mRippleView) * 2,
        (mPlusView.width / 2).toFloat())
    mRippleView.show()
    animator.interpolator = DecelerateInterpolator()
    animator.duration = 400
    animator.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator?) {
        mContentLayout.animate().alpha(1f)
      }

      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        mPlusView.visibility = View.VISIBLE
        mRippleView.visibility = View.INVISIBLE
      }
    })
    animator.start()
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  private fun startRippleTransitionReveal() {
    mPlusView.visibility = View.INVISIBLE
    val animator: Animator = ViewAnimationUtils.createCircularReveal(mRippleView,
        (mPlusView.x + mPlusView.width / 2).toInt(), mPlusView.y.toInt(),
        (mPlusView.width / 2).toFloat(), ViewUtil.getViewRadius(mRippleView) * 2)
    mRippleView.show()
    animator.interpolator = AccelerateInterpolator()
    animator.duration = 500
    animator.addListener(object : AnimatorListenerAdapter() {

      override fun onAnimationStart(animation: Animator?) {
        mContentLayout.animate().alpha(0f)
      }

      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        startActivity()
      }
    })
    animator.start()
  }

  fun startActivity() {
    val intent = Intent(this, NewWordActivity::class.java)
    ActivityCompat.startActivity(this, intent,
        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
    launchedActivity = true
  }
}
