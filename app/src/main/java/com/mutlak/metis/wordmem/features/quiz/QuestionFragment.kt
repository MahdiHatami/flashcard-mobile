package com.mutlak.metis.wordmem.features.quiz

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.data.model.pojo.Question
import com.mutlak.metis.wordmem.features.quiz.widgets.AnswerAdapter
import com.mutlak.metis.wordmem.features.quiz.widgets.GridSpacingItemDecoration
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import io.realm.Realm


class QuestionFragment : Fragment() {
  private var mQuestion: Question? = null
  private var adapter: AnswerAdapter? = null
  lateinit var realm: Realm
  private var setttings: Settings? = null


  companion object {
    val CURRENT_WORD = "current_word"
    private val SESSION_ID = "session_id"

    @JvmStatic
    fun create(wordString: String, sessionId: Int): QuestionFragment {
      val fragment = QuestionFragment()
      val args = Bundle()
      args.putString(CURRENT_WORD, wordString)
      args.putInt(SESSION_ID, sessionId)
      fragment.arguments = args
      return fragment
    }

  }

  @BindView(R.id.question_word) lateinit var mEnglish: TextView
  @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val mWordString = arguments?.getString(CURRENT_WORD)
    mQuestion = Gson().fromJson(mWordString, Question::class.java)
    realm = Realm.getDefaultInstance()
    setttings = realm.where(Settings::class.java).findFirst()
    setttings = realm.copyFromRealm(setttings)
  }

  private fun dpToPx(dp: Int): Int {
    val r = resources
    return Math.round(
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    val rootView = inflater.inflate(R.layout.fragment_question, container, false) as ViewGroup
    ButterKnife.bind(this, rootView)

    mEnglish.text = mQuestion!!.question.english

    Handler().postDelayed({ this.setupAnswers() }, 300)
    return rootView
  }

  private fun setupAnswers() {
    var col = 1
    if (setttings!!.quizType == 0) setttings!!.quizType = SettingsActivity.QUIZ_TYPE_WORD
    if (setttings!!.quizType == SettingsActivity.QUIZ_TYPE_WORD) col = 2
    adapter = activity?.let { AnswerAdapter(it, mQuestion!!, setttings!!.quizType) }
    val mLayoutManager = GridLayoutManager(activity, col)
    recyclerView.layoutManager = mLayoutManager
    recyclerView.addItemDecoration(GridSpacingItemDecoration(col, dpToPx(10), true))
    recyclerView.itemAnimator = DefaultItemAnimator()
    recyclerView.isNestedScrollingEnabled = false
    recyclerView.adapter = adapter
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
  }


}
