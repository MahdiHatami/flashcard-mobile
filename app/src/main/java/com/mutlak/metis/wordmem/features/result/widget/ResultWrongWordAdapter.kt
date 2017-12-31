package com.mutlak.metis.wordmem.features.result.widget


import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.pojo.Question
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.features.settings.SettingsActivity

class ResultWrongWordAdapter(private val mContext: Context, private val settings: Settings,
    private val questions: List<Question>) : RecyclerView.Adapter<ResultWrongWordAdapter.AnswerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
    val itemView = LayoutInflater.from(parent.context)
        .inflate(R.layout.result_wrong_list_item, parent, false)

    return AnswerViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
    val question = questions[position]
    holder.cardView.background = ContextCompat.getDrawable(mContext,
        R.drawable.answer_card_wrong_border)
    holder.word.text = question.question.english
    when (settings.quizType) {
      SettingsActivity.QUIZ_TYPE_WORD -> {
        holder.correctAnswer.text = question.question.singleTurkish
        holder.wrongAnswer.text = question.userResponse!!.singleTurkish
      }
      SettingsActivity.QUIZ_TYPE_SENTENCE -> {
        holder.correctAnswer.text = question.question.meaning
        holder.wrongAnswer.text = question.userResponse!!.meaning
      }
    }
  }

  override fun getItemCount(): Int {
    return questions.size
  }

  inner class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var word: TextView
    var correctAnswer: TextView
    var wrongAnswer: TextView
    var cardView: CardView

    init {

      cardView = view.findViewById<View>(R.id.result_wrong_cardview) as CardView
      word = view.findViewById<View>(R.id.result_item_word) as TextView
      correctAnswer = view.findViewById<View>(R.id.result_item_correct_meaning) as TextView
      wrongAnswer = view.findViewById<View>(R.id.result_item_user_answer) as TextView
    }
  }
}
