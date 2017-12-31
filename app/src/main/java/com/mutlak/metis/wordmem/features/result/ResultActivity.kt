package com.mutlak.metis.wordmem.features.result

import android.content.*
import android.os.*
import android.support.annotation.*
import android.support.design.widget.*
import android.support.v7.widget.*
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.*
import android.view.animation.*
import android.widget.*
import butterknife.*
import com.google.android.gms.ads.*
import com.google.gson.*
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.*
import com.mutlak.metis.wordmem.data.model.pojo.ExamSession
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.features.landing.*
import com.mutlak.metis.wordmem.features.quiz.*
import com.mutlak.metis.wordmem.features.result.widget.*
import com.mutlak.metis.wordmem.features.review.*
import timber.log.*
import uk.co.chrisjenx.calligraphy.*
import java.util.*
import javax.inject.*


class ResultActivity : BaseActivity(), ResultView {

  companion object {
    val AD_INTERSTITIAL = "ca-app-pub-3882496792252518/2230803188"
    val AD_NATIVE_EXPRESS = "ca-app-pub-3882496792252518/9939515587"

  }

  private val TAG = "ResultActivity"
  private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

  private lateinit var examSession: ExamSession
  private var mSettings: Settings? = null

  lateinit var mInterstitial: InterstitialAd

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

    val request = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()

    mInterstitial = InterstitialAd(this)
    mInterstitial.adUnitId = AD_INTERSTITIAL
    mInterstitial.loadAd(request)
    mInterstitial.adListener = object : AdListener() {
      override fun onAdLoaded() {
        mInterstitial.show()
      }
    }

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
    mInterstitial.adListener = null
    super.onDestroy()
  }

}
