package com.mutlak.metis.wordmem.features.review

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.github.florent37.tutoshowcase.TutoShowcase
import com.github.zagum.switchicon.SwitchIconView
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.local.PreferencesHelper
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.base.BaseFragment
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import java.util.Date
import javax.inject.Inject


class ReviewFragment : BaseFragment(), ReviewView, View.OnClickListener {


  private var checkDirection: Boolean = false
  private var scrollStarted: Boolean = false
  private var showCaseView: ShowcaseView? = null
  private var counter = 0
  private var isShowViewPresented: Boolean = false

  private var currentWord: Word? = null
  private var reviewType: Int = 0
  private var mAlertDialog: MaterialDialog? = null

  @Inject lateinit var mPresenter: ReviewPresenter
  @Inject lateinit var mPref: PreferencesHelper

  @BindView(R.id.toolbar_counter) lateinit var mToolbarCounter: TextView
  @BindView(R.id.learnt_switch) lateinit var mLearnt: SwitchIconView
  @BindView(R.id.ignore_switch) lateinit var mIgnore: SwitchIconView
  @BindView(R.id.bookmark_switch) lateinit var mBookmark: SwitchIconView

  @BindView(R.id.learnt_switch_layout) lateinit var mLearntLayout: LinearLayout
  @BindView(R.id.ignore_layout) lateinit var mIgnoreLayout: LinearLayout
  @BindView(R.id.bookmark_layout) lateinit var mFlagLayout: LinearLayout

  @BindView(R.id.learnt_text) lateinit var mLearntText: TextView
  @BindView(R.id.ignore_text) lateinit var mIgnoreText: TextView
  @BindView(R.id.bookmark_text) lateinit var mFlagText: TextView
  @BindView(R.id.review_action_layout) lateinit var mActionLayout: LinearLayout
  @BindView(R.id.drawer_layout) lateinit var frameLayout: FrameLayout
  @BindView(R.id.toolbar) lateinit var mToolbar: Toolbar
  @BindView(R.id.view_pager) lateinit var mViewPager: ViewPager

  init {
    setHasOptionsMenu(true)
  }

  override val layout: Int
    get() = R.layout.fragment_review

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fragmentComponent().inject(this)
    mPresenter.attachView(this)
    reviewType = activity.intent.extras.getInt(LandingActivity.REVIEW_TYPE)

    isShowViewPresented = mPref.getBoolean(ShowView.REVIEW, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    mPresenter.startCards(reviewType)

    setupToolbar()
    changeStatusBarColor()
    setupViewPager()
    setupActionButtons()
  }

  private fun displayShowcase() {
    if (!isShowViewPresented) {
      showCaseView = ShowcaseView.Builder(activity).setTarget(ViewTarget(mLearnt))
          .setStyle(R.style.CustomShowcaseTheme)
          .setContentTitle(getString(R.string.showcase_learnt_title))
          .setContentText(getString(R.string.showcase_learnt_content))
          .setOnClickListener(this)
          .build()
      showCaseView!!.setButtonText(getString(R.string.next))

      mPref.putBoolean(ShowView.REVIEW, true)
    }
  }

  private fun setupActionButtons() {
    mLearntLayout.setOnClickListener { _ ->
      mLearnt.switchState()
      currentWord!!.learnt = mLearnt.isIconEnabled
      mPresenter.updateWord(currentWord!!)
    }
    mIgnoreLayout.setOnClickListener { _ ->
      mIgnore.switchState()
      currentWord?.ignored = mIgnore.isIconEnabled
      mPresenter.updateWord(currentWord!!)
    }
    mFlagLayout.setOnClickListener { _ ->
      mBookmark.switchState()
      currentWord?.bookmarked = mBookmark.isIconEnabled
      mPresenter.updateWord(currentWord!!)
    }
  }

  private fun setupViewPager() {
    mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (checkDirection) {
          if (thresholdOffset > positionOffset) {
            // going right
          } else {
            // going left
          }
          checkDirection = false
        }
      }

      override fun onPageSelected(position: Int) {
        currentWord?.totalReview = currentWord?.totalReview?.plus(1)!!
        currentWord?.lastSeen = Date()
        mPresenter.updateWord(currentWord!!)
        mPresenter.nextWord(position)
      }

      override fun onPageScrollStateChanged(state: Int) {
        if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
          scrollStarted = true
          checkDirection = true
        } else {
          scrollStarted = false
        }
      }
    })
  }

  override fun changeCounterTitle(s: String) {
    mToolbarCounter.text = s
  }

  override fun changeCurrentWord(word: Word) {
    currentWord = word
  }

  override fun changeIconTextColor() {
    if (mLearnt.isIconEnabled) {
      mLearntText.setTextColor(ContextCompat.getColor(context, R.color.primary))
    } else {
      mLearntText.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    if (mIgnore.isIconEnabled) {
      mIgnoreText.setTextColor(ContextCompat.getColor(context, R.color.accent))
    } else {
      mIgnoreText.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    if (mBookmark.isIconEnabled) {
      mFlagText.setTextColor(ContextCompat.getColor(context, R.color.yellow))
    } else {
      mFlagText.setTextColor(ContextCompat.getColor(context, R.color.white))
    }
  }

  override fun changeSwitchesState() {
    mLearnt.isIconEnabled = currentWord!!.learnt
    mIgnore.isIconEnabled = currentWord!!.ignored
    mBookmark.isIconEnabled = currentWord!!.bookmarked
  }

  override fun hideLoading() {

  }

  private fun setupToolbar() {
    val activity = activity as AppCompatActivity
    activity.setSupportActionBar(mToolbar)
    if (activity.supportActionBar != null) {
      activity.supportActionBar!!.setDisplayShowHomeEnabled(true)
      activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
      activity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_clear)
    }
  }

  override fun initWordsPager(list: List<Word>) {
    val adapter = WordCardAdapter(list, childFragmentManager)
    mViewPager.adapter = adapter

    if (currentWord != null) {
      currentWord = list.iterator().next()
      currentWord!!.lastSeen = Date()
      currentWord!!.totalReview = currentWord!!.totalReview.plus(1)
      mPresenter.updateWord(currentWord!!)
      changeSwitchesState()
    }

    displayShowcase()
  }

  private fun redirectBack() {
    activity.finish()
  }

  private fun changeStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.window.statusBarColor = ContextCompat.getColor(context, R.color.primary_dark)
    }
  }

  override fun showAlert(title: Int, content: Int) {
    if (mAlertDialog == null) {
      mAlertDialog = MaterialDialog.Builder(activity).title(title)
          .content(content)
          .cancelable(false)
          .positiveText(R.string.ok)
          .onPositive { _, _ -> redirectBack() }
          .show()
    } else {
      mAlertDialog!!.setContent(content)
      mAlertDialog!!.show()
    }
  }

  override fun showLastPageView() {}

  override fun hideToolbar() {
    mToolbarCounter.visibility = View.GONE
    mActionLayout.visibility = View.GONE
  }

  override fun showToolbar() {
    mToolbarCounter.visibility = View.VISIBLE
    mActionLayout.visibility = View.VISIBLE
  }

  override fun onClick(v: View) {
    when (counter) {
      0 -> {
        showCaseView!!.setShowcase(ViewTarget(mIgnore), true)
        showCaseView!!.setContentTitle(getString(R.string.showcase_ignore_title))
        showCaseView!!.setContentText(getString(R.string.showcase_ignore_content))
      }

      1 -> {
        showCaseView!!.setShowcase(ViewTarget(mBookmark), true)
        showCaseView!!.setContentTitle(getString(R.string.showcase_favorite_title))
        showCaseView!!.setContentText(getString(R.string.showcase_favorite_content))
      }

      2 -> {
        showCaseView!!.setShowcase(ViewTarget(frameLayout), true)
        showCaseView!!.setContentTitle(getString(R.string.showcase_image_title))
        showCaseView!!.setContentText(getString(R.string.showcase_image_content))
        showCaseView!!.setButtonText(getString(R.string.close))
      }
      3 -> {
        showCaseView!!.hide()
        setAlpha(1.0f, mLearnt, mIgnore, mBookmark)
        displaySwipe()
      }
    }
    counter++
  }

  private fun displaySwipe() {
    TutoShowcase.from(activity)
        .setContentView(R.layout.review_show_case)
        .on(R.id.card_view)
        .displaySwipableLeft()
        .delayed(400)
        .animated(true)
        .show()
  }

  private fun setAlpha(alpha: Float, vararg views: View) {
    for (view in views) {
      view.alpha = alpha
    }
  }

  companion object {

    private val TAG = ReviewFragment::class.java.simpleName
    private val thresholdOffset = 0.5f

    fun newInstance(): ReviewFragment {
      return ReviewFragment()
    }
  }


  object ShowView {
    val REVIEW = "review_show_case"
  }
}
