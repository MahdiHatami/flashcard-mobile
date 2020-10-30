package com.mutlak.metis.wordmem.features.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import butterknife.BindView
import butterknife.OnClick
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.data.model.pojo.ExamSession
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.result.widget.CircleProgressView
import com.mutlak.metis.wordmem.features.result.widget.ResultWrongWordAdapter
import com.mutlak.metis.wordmem.features.review.ReviewActivity
import com.mutlak.metis.wordmem.util.AdsUtil
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.Locale
import javax.inject.Inject


class ResultActivity : BaseActivity(), ResultView {

  companion object {
    const val AD_INTERSTITIAL = "ca-app-pub-4458047788519500/6772225515"
  }

  private val TAG = "ResultActivity"
  private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

  private lateinit var examSession: ExamSession
  private var mSettings: Settings? = null

  private lateinit var mInterstitialAd: InterstitialAd


  @Inject lateinit var mPresenter: ResultPresenter


  @BindView(R.id.circle_progress_view) lateinit var mCircleProgressView: CircleProgressView
  @BindView(R.id.wrong_answer) lateinit var mWrongText: TextView
  @BindView(R.id.right_answer) lateinit var mRightText: TextView

  @BindView(R.id.result_examine_subtitle_textview) lateinit var mExamineText: TextView
  @BindView(R.id.result_explore_subtitle_textview) lateinit var mExploreText: TextView
  @BindView(R.id.result_quiz_subtitle_textview) lateinit var mQuizText: TextView

  @BindView(R.id.result_examine_linear) lateinit var mExamineBtn: LinearLayout
  @BindView(R.id.result_explore_linear) lateinit var mExploreBtn: LinearLayout
  @BindView(R.id.result_quiz_linear) lateinit var mQuizBtn: LinearLayout
  @BindView(R.id.result_home_linear) lateinit var mHomeBtn: LinearLayout

  @BindView(R.id.bottom_sheet) lateinit var mBottomSheet: RelativeLayout
  @BindView(R.id.result_wrong_recyclerview) lateinit var recyclerView: RecyclerView


  override val layout: Int get() = R.layout.activity_result

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    changeStatusBarColor(R.color.landing_header_start)

    mSettings = mPresenter.getSetting()

    // todo: commend  lines i was on harry look agai to solve the error :)
//    MobileAds.initialize(this, AdsUtil.APP_ID)
    mInterstitialAd = InterstitialAd(this)
    mInterstitialAd.adUnitId = AD_INTERSTITIAL
    val request = AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // An example device ID
        .build()
//    mInterstitialAd.loadAd(request)


    if (intent.hasExtra(QuizActivity.EXAM_SESSION)) {
      val s = intent.getSerializableExtra(QuizActivity.EXAM_SESSION).toString()
      examSession = Gson().fromJson(s, ExamSession::class.java)
    }

    val totalQuestions = examSession.questions!!.size

    val correct = examSession.questions!!.filter { it.isUserAnswerWasCorrect }.count()
    val wrong = totalQuestions - correct
    val rate = correct * 100 / totalQuestions

    mWrongText.text = wrong.toString()
    mRightText.text = correct.toString()

    mExamineText.text = String.format(Locale.US, "%d %s", wrong, getString(R.string.wrong))
    mExploreText.text = String.format(Locale.US, "%d %s", mSettings?.reviewLimit,
        getString(R.string.words))
    mQuizText.text = String.format(Locale.US, "%d %s", mSettings?.quizLimit,
        getString(R.string.words))

    mCircleProgressView.isTextEnabled = true
    mCircleProgressView.interpolator = AccelerateDecelerateInterpolator() as Interpolator?
    mCircleProgressView.setProgressWithAnimation(rate.toFloat(), 1000)

    mBottomSheetBehavior = BottomSheetBehavior.from<View>(mBottomSheet)
    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    mBottomSheetBehavior.setBottomSheetCallback(
        object : BottomSheetBehavior.BottomSheetCallback() {
          override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}

          override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            when (newState) {
              BottomSheetBehavior.STATE_SETTLING -> Timber.i(TAG, "onStateChanged: settle")
              BottomSheetBehavior.STATE_COLLAPSED -> Timber.i(TAG, "onStateChanged: collapsed")
              BottomSheetBehavior.STATE_EXPANDED -> Timber.i(TAG, "onStateChanged: expanded")
            }
          }
        })

  }

  @OnClick(R.id.result_quiz_linear)
  fun quizOnclick() {
    val i = Intent(this@ResultActivity, QuizActivity::class.java)
    startActivity(i)
  }

  @OnClick(R.id.result_explore_linear)
  fun exploreOnclick() {
    val intent = Intent(this@ResultActivity, ReviewActivity::class.java)
    intent.putExtra(LandingActivity.REVIEW_TYPE, LandingActivity.REVIEW_TYPE_NEW)
    startActivity(intent)
  }

  @OnClick(R.id.result_examine_linear)
  fun examineOnclick() {

    val list = examSession.questions?.filterNot { it.isUserAnswerWasCorrect }
    if (list!!.isNotEmpty()) {
      val adapter = ResultWrongWordAdapter(this, mSettings!!, list)
      val mLayoutManager: LayoutManager = GridLayoutManager(this@ResultActivity, 1)
      recyclerView.layoutManager = mLayoutManager
      recyclerView.itemAnimator = DefaultItemAnimator()
      recyclerView.adapter = adapter
      mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    } else {
      Toast.makeText(this, R.string.result_examine_no_wrong_message, Toast.LENGTH_SHORT).show()
    }
  }

  @OnClick(R.id.result_home_linear)
  fun homeOnClick() {
    finish()
    startActivity(Intent(this@ResultActivity, LandingActivity::class.java))
  }

  override fun onBackPressed() {
    finish()
    startActivity(Intent(this@ResultActivity, LandingActivity::class.java))
  }

  override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
  }

  override fun getContext(): Context {
    return this
  }

  override fun onDestroy() {
    mPresenter.detachView()
    mInterstitialAd.adListener = null
    super.onDestroy()
  }

}
