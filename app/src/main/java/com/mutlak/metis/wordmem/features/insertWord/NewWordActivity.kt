package com.mutlak.metis.wordmem.features.insertWord

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.mlsdev.rximagepicker.RxImageConverters
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.R.string
import com.mutlak.metis.wordmem.data.model.Sentense
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.extension.afterTextChanged
import com.mutlak.metis.wordmem.extension.hide
import com.mutlak.metis.wordmem.extension.show
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import io.realm.RealmList
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class NewWordActivity : BaseActivity(), NewWordView {

  @Inject
  lateinit var mPresenter: NewWordPresenter

  val TAG: String = NewWordActivity::class.java.simpleName
  private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
  private var isFormValid: Boolean = false
  private var mFile: File? = null

  @BindView(R.id.toolbar) lateinit var mToolbar: Toolbar
  @BindView(R.id.input_word) lateinit var mTextWord: TextInputEditText
  @BindView(R.id.input_meaning) lateinit var mTextMeaning: EditText
  @BindView(R.id.input_sentence) lateinit var mTextSentence: EditText
  @BindView(R.id.input_type) lateinit var mTextType: EditText
  @BindView(R.id.input_layout_word) lateinit var mTextLayoutWord: TextInputLayout
  @BindView(R.id.input_layout_meaning) lateinit var mTextLayoutMeaning: TextInputLayout
  @BindView(R.id.new_word_bottom_sheet) lateinit var mBottomSheet: LinearLayout
  @BindView(R.id.frame_image_section) lateinit var mFrameUpload: FrameLayout
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
        if (isFormValid())
          doSubmit()
        true
      }
      else -> {
        return super.onOptionsItemSelected(item)
      }
    }
  }

  private fun isFormValid(): Boolean {
    var isValid = true

    if (mTextWord.text.isEmpty()) {
      isValid = false
      mTextLayoutWord.error = getString(string.word_is_required)
    }
    if (mTextWord.text.length < 2) {
      isValid = false
      mTextLayoutWord.error = getString(string.word_min_length, 2)
    }

    if (mTextMeaning.text.isEmpty()) {
      isValid = false
      mTextLayoutMeaning.error = getString(string.word_is_required)
    }
    if (mTextMeaning.text.length < 5) {
      isValid = false
      mTextLayoutMeaning.error = getString(string.word_min_length, 5)
    }

    return isValid
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
    mPresenter.saveWord(word, mFile)
  }

  @OnClick(R.id.frame_image_section)
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
    RxImagePicker.with(this).requestImage(source).flatMap { uri ->
      RxImageConverters.uriToFile(this, uri, File.createTempFile("image", ".jpg"))
    }.subscribe({
      mFrameUpload.hide()
      mImageSelected.show()
      Glide.with(this).load(it).asBitmap().into(mImageSelected)
      mFile = it
    })
  }

  override fun cleanForm() {
    mTextWord.text.clear()
    mTextMeaning.text.clear()
    mTextSentence.text.clear()
    mTextType.text.clear()
    mImageSelected.hide()
    mFrameUpload.show()

    mTextWord.requestFocus()
  }

  override fun showAlert(title: Int, message: Int, type: Int) {
    SweetAlertDialog(this, type).setTitleText(getString(title))
        .setContentText(getString(message))
        .show()
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }

  override fun showError(throwable: Throwable) {
    Timber.e(throwable, "There was a problem retrieving the ...")
  }

  override fun getContext(): Context {
    return this
  }
}
