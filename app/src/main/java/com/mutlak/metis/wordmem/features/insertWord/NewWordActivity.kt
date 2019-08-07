package com.mutlak.metis.wordmem.features.insertWord

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
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
import io.reactivex.disposables.Disposable
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_new_word.constraintTurkish
import kotlinx.android.synthetic.main.activity_new_word.frameImageSection
import kotlinx.android.synthetic.main.activity_new_word.imageSelected
import kotlinx.android.synthetic.main.activity_new_word.inputLayoutMeaning
import kotlinx.android.synthetic.main.activity_new_word.inputLayoutTurkish
import kotlinx.android.synthetic.main.activity_new_word.inputLayoutWord
import kotlinx.android.synthetic.main.activity_new_word.inputMeaning
import kotlinx.android.synthetic.main.activity_new_word.inputSentence
import kotlinx.android.synthetic.main.activity_new_word.inputTurkish
import kotlinx.android.synthetic.main.activity_new_word.inputType
import kotlinx.android.synthetic.main.activity_new_word.inputWord
import kotlinx.android.synthetic.main.activity_new_word.toolbarNewWord
import kotlinx.android.synthetic.main.choose_photo_source.bottomSheetNewWord
import timber.log.Timber
import java.io.File
import java.util.Locale
import javax.inject.Inject


class NewWordActivity : BaseActivity(), NewWordView {

  @Inject
  lateinit var mPresenter: NewWordPresenter

  private var disposable: Disposable? = null
  private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
  private var isFormValid: Boolean = false
  private var mFile: File? = null

  override val layout: Int
    get() = R.layout.activity_new_word

  private val userLanguage: String = Locale.getDefault().language

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    mPresenter.attachView(this)

    changeStatusBarColor(R.color.landing_plus_darker)

    setupToolbar()

    setupBottomSheet()

    if (userLanguage != "tr") {
      constraintTurkish.visibility = View.GONE
    }

    inputWord.afterTextChanged {
      if (inputWord.text.toString().trim().isEmpty()) {
        inputLayoutWord.error = getString(string.word_is_required)
        isFormValid = false
      } else {
        isFormValid = true
        inputLayoutWord.error = null
      }
    }
  }

  private fun setupBottomSheet() {

    mBottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheetNewWord)
    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    mBottomSheetBehavior.setBottomSheetCallback(
        object : BottomSheetBehavior.BottomSheetCallback() {
          override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}

          override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {}
        })

  }

  private fun setupToolbar() {
    setSupportActionBar(toolbarNewWord)
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

    if (inputWord.text.isEmpty()) {
      isValid = false
      inputLayoutWord.error = getString(string.word_is_required)
    }
    if (inputWord.text.length < 2) {
      isValid = false
      inputLayoutWord.error = getString(string.word_min_length, 2)
    }

    if (userLanguage == "tr") {
      if (inputTurkish.text.isEmpty()) {
        isValid = false
        inputLayoutTurkish.error = getString(string.word_is_required)
      }
      if (inputTurkish.text.length < 2) {
        isValid = false
        inputLayoutTurkish.error = getString(string.word_min_length, 2)
      }
    }

    if (inputMeaning.text.isEmpty()) {
      isValid = false
      inputLayoutMeaning.error = getString(string.word_is_required)
    }
    if (inputMeaning.text.length < 5) {
      isValid = false
      inputLayoutMeaning.error = getString(string.word_min_length, 5)
    }

    return isValid
  }

  private fun doSubmit() {
    val english = inputWord.text.toString().trim()
    val turkish = inputTurkish.text.toString().trim()
    val meaning = inputMeaning.text.toString().trim()
    val sen = inputSentence.text.toString().trim()
    val sentence = Sentense(title = sen)
    val sentences: RealmList<Sentense> = RealmList()
    sentences.add(sentence)
    val type = inputType.text.toString().trim()
    val word = Word(english = english, turkish = turkish, meaning = meaning, sentences = sentences,
        type = type)
    mPresenter.saveWord(word, mFile)
  }

  @OnClick(R.id.frameImageSection)
  fun imageOnClick() {
    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
      if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED ||
          mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {

        val outRect = Rect()
        bottomSheetNewWord.getGlobalVisibleRect(outRect)

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
    disposable = RxImagePicker.with(this).requestImage(source).flatMap { uri ->
      RxImageConverters.uriToFile(this, uri, File.createTempFile("image", ".jpg"))
    }.subscribe {
      frameImageSection.hide()
      imageSelected.show()
      Glide.with(this).load(it).asBitmap().into(imageSelected)
      mFile = it
    }
  }

  override fun cleanForm() {
    inputWord.text.clear()
    inputMeaning.text.clear()
    inputTurkish.text.clear()
    inputSentence.text.clear()
    inputType.text.clear()
    imageSelected.hide()
    frameImageSection.show()

    inputWord.requestFocus()
  }

  override fun onDestroy() {
    disposable?.dispose()
    super.onDestroy()
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
