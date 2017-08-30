package com.mutlak.metis.wordmem.features.insertWord

import android.content.*
import android.graphics.*
import android.os.*
import android.support.annotation.*
import android.support.design.widget.*
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import butterknife.*
import com.mlsdev.rximagepicker.*
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.R.string
import com.mutlak.metis.wordmem.data.model.*
import com.mutlak.metis.wordmem.extension.*
import com.mutlak.metis.wordmem.features.base.*
import io.realm.*
import pl.bclogic.pulsator4droid.library.*
import timber.log.*
import javax.inject.*


class NewWordActivity : BaseActivity(), NewWordView {

  @Inject lateinit var mPresenter: NewWordPresenter

  val TAG: String = NewWordActivity::class.java.simpleName

  private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

  var isFormValid: Boolean = false

  @BindView(R.id.toolbar) lateinit var mToolbar: Toolbar
  @BindView(R.id.pulsator) lateinit var mPulsator: PulsatorLayout

  @BindView(R.id.input_word) lateinit var mTextWord: TextInputEditText
  @BindView(R.id.input_meaning) lateinit var mTextMeaning: EditText
  @BindView(R.id.input_sentence) lateinit var mTextSentence: EditText
  @BindView(R.id.input_type) lateinit var mTextType: EditText

  @BindView(R.id.input_layout_word) lateinit var mTextLayoutWord: TextInputLayout

  @BindView(R.id.new_word_bottom_sheet) lateinit var mBottomSheet: LinearLayout
  @BindView(R.id.image_holder) lateinit var mImageHolder: ImageView
  @BindView(R.id.frame_upload_image) lateinit var mFrameUpload: FrameLayout
  @BindView(R.id.image_selected) lateinit var mImageSelected: ImageView

  override val layout: Int
    get() = R.layout.activity_new_word

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    changeStatusBarColor(R.color.landing_plus_darker)

    setupToolbar()

    setupBottomSheet()
    mTextWord.afterTextChanged {
      if (mTextWord.text.toString().trim().isEmpty()) {
        mTextLayoutWord.error = getString(R.string.word_is_required)
        isFormValid = false
      } else {
        isFormValid = true
        mTextLayoutWord.error = null
      }
    }

    mPulsator.start()
  }

  private fun setupBottomSheet() {

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

  private fun setupToolbar() {
    setSupportActionBar(mToolbar)
    if (supportActionBar != null) {
      supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      supportActionBar!!.setDisplayShowHomeEnabled(true)
      supportActionBar!!.title = getString(string.insert_word_title)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_new_word, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_save_word -> {
        doSubmit()
        true
      }
      else -> {
        return super.onOptionsItemSelected(item)
      }
    }
  }

  private fun doSubmit() {
    val english = mTextWord.text.toString().trim()
    val meaning = mTextMeaning.text.toString().trim()
    val sen = mTextSentence.text.toString().trim()
    val sentence = Sentense(title = sen)
    val sentences: RealmList<Sentense> = RealmList()
    sentences.add(sentence)
    val type = mTextType.text.toString().trim()
    val word = Word(english = english, meaning = meaning, sentences = sentences, type = type)
    if (!isFormValid) {
      mTextLayoutWord.error = getString(string.word_is_required)
    } else {
      mTextLayoutWord.error = null
      mPresenter.sendWord(word)
    }
  }

  @OnClick(R.id.image_holder)
  fun imageOnClick() {
    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
      if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED ||
          mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {

        val outRect = Rect()
        mBottomSheet.getGlobalVisibleRect(outRect)

        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
          mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
      }
    }

    return super.dispatchTouchEvent(event)
  }

  @OnClick(R.id.source_camera)
  fun cameraOnClick() {
    pickImage(Sources.CAMERA)
  }

  @OnClick(R.id.source_gallery)
  fun galleryOnClick() {
    pickImage(Sources.GALLERY)
  }

  private fun pickImage(source: Sources) {
    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    RxImagePicker.with(this).requestImage(source).flatMap {
      RxImageConverters.uriToBitmap(applicationContext, it)
    }.subscribe({
      mFrameUpload.hide()
      mImageSelected.setImageBitmap(it)
    })
  }

  override fun showError(throwable: Throwable) {
    // show error
    Timber.e(throwable, "There was a problem retrieving the pokemon...")
  }

  override fun getContext(): Context {
    return this
  }
}
