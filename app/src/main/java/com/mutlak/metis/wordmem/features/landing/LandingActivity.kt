package com.mutlak.metis.wordmem.features.landing

import android.animation.*
import android.content.*
import android.os.*
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.annotation.*
import android.support.constraint.*
import android.support.design.widget.*
import android.support.v4.app.*
import android.support.v4.content.*
import android.view.*
import android.view.WindowManager.LayoutParams
import android.view.animation.*
import android.widget.*
import butterknife.*
import com.afollestad.materialdialogs.*
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.extension.*
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.features.insertWord.*
import com.mutlak.metis.wordmem.features.quiz.*
import com.mutlak.metis.wordmem.features.result.widget.*
import com.mutlak.metis.wordmem.features.review.*
import com.mutlak.metis.wordmem.features.settings.*
import com.mutlak.metis.wordmem.util.*
import com.victor.loading.book.*
import javax.inject.*


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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && launchedActivity) {
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


  private fun setupView() {
    mTakeQuizView.setOnClickListener {
      startActivity(Intent(this, QuizActivity::class.java))
    }
    mSettings.setOnClickListener {

      startActivity(Intent(this, SettingsActivity::class.java))
    }
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

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

  @OnClick(R.id.linear_plus)
  fun plusOnClick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      startRippleTransitionReveal()
    } else {
      startActivity()
    }
  }
}
