package com.mutlak.metis.wordmem.features.review

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Word
import java.util.HashMap
import java.util.Locale


class CardFragment : Fragment() {

  private var mWord: Word? = null
  private var mShowingBack = false
  private var height: Int = 0
  private var tts: TextToSpeech? = null

  init {
    setHasOptionsMenu(true)
  }

  @BindView(R.id.word_text) lateinit var mWordText: TextView
  @BindView(R.id.word_english_meaning) lateinit var mMeaning: TextView
  @BindView(R.id.word_sentence) lateinit var mSentence: TextView
  @BindView(R.id.word_image) lateinit var mWordImage: ImageView
  @BindView(R.id.card_view) lateinit var mCard: CardView
  @BindView(R.id.turkish_meaning) lateinit var mTurkish: TextView
  @BindView(R.id.word_type) lateinit var mType: TextView
  @BindView(R.id.example_text) lateinit var mExampleText: TextView
  @BindView(R.id.word_voice) lateinit var mVoiceBtn: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val mWordString = arguments?.getString(CURRENT_WORD)
    mWord = Gson().fromJson(mWordString, Word::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    val rootView = inflater.inflate(R.layout.fragment_card, container, false) as ViewGroup
    ButterKnife.bind(this, rootView)

    setupTextToSpeech()

    mCard.setOnClickListener { flipCard() }

    mWordText.text = mWord!!.english
    mMeaning.text = mWord!!.meaning
    Glide.with(activity)
        .load<Any>("http://mutlakyazilim.com/kelimeEzber/" + mWord!!.image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(mWordImage)
    mTurkish.text = mWord!!.turkish

    if (mWord?.type == null) {
      mType.visibility = View.GONE
    } else {
      mType.text = mWord!!.type
    }

    if (mWord!!.singleSentence.isEmpty()) {
      mExampleText.visibility = View.GONE
    } else {
      mSentence.text = mWord!!.singleSentence
    }

    return rootView
  }

  private fun setupTextToSpeech() {
    tts = TextToSpeech(activity) { status ->
      if (status != TextToSpeech.ERROR) {
        tts!!.language = Locale.US
      }
    }
    tts!!.setPitch(0.8f)
    tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String) {
        mVoiceBtn.isEnabled = false
      }

      override fun onDone(utteranceId: String) {
        mVoiceBtn.isEnabled = true
        mVoiceBtn.setImageDrawable(
            activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_voice) })
      }

      override fun onError(utteranceId: String) {
        Toast.makeText(activity, R.string.voice_not_available, Toast.LENGTH_SHORT).show()
      }
    })
  }

  @OnClick(R.id.word_voice)
  fun voiceOnClick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ttsGreater21(mWord!!.english)
    } else {
      ttsUnder20(mWord!!.english)
    }
  }

  private fun ttsUnder20(text: String) {
    val map = HashMap<String, String>()
    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId")
    tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, map)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) private fun ttsGreater21(text: String) {
    val utteranceId = this.hashCode().toString() + ""
    tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      R.id.nav_flip -> {
        flipCard()
        return true
      }
    }

    return false
  }

  private fun flipCard() {
    val handler = Handler()
    if (mShowingBack) {
      mCard.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
      handler.postDelayed({
        hideBack()
        mCard.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
      }, 150)
      mShowingBack = false
      return
    }
    height = mCard.height
    mCard.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
    handler.postDelayed({
      hideFront()
      mCard.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
    }, 150)
    mShowingBack = true
  }

  private fun hideFront() {
    mWordText.visibility = View.GONE
    mWordImage.visibility = View.GONE
    mSentence.visibility = View.GONE
    mMeaning.visibility = View.GONE
    mType.visibility = View.GONE
    mExampleText.visibility = View.GONE
    mTurkish.visibility = View.VISIBLE
    mVoiceBtn.visibility = View.VISIBLE
    mCard.minimumHeight = height
  }

  private fun hideBack() {
    mWordText.visibility = View.VISIBLE
    mWordImage.visibility = View.VISIBLE
    mSentence.visibility = View.VISIBLE
    mMeaning.visibility = View.VISIBLE
    if (mWord?.type?.isEmpty()!!) {
      mType.visibility = View.GONE
    } else {
      mType.text = mWord!!.type
    }
    mType.visibility = View.VISIBLE
    mExampleText.visibility = View.VISIBLE
    mTurkish.visibility = View.GONE
    mVoiceBtn.visibility = View.GONE
    mCard.minimumHeight = height
  }

  override fun onDestroy() {
    super.onDestroy()
    if (tts != null) {
      tts!!.stop()
      tts!!.shutdown()
    }
  }

  companion object {

    val CURRENT_WORD = "current_word"

    fun create(wordString: String): CardFragment {
      val fragment = CardFragment()
      val args = Bundle()
      args.putString(CURRENT_WORD, wordString)
      fragment.arguments = args
      return fragment
    }
  }
}
