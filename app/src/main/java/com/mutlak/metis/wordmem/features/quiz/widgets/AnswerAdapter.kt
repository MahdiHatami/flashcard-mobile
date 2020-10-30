package com.mutlak.metis.wordmem.features.quiz.widgets

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.pojo.Answer
import com.mutlak.metis.wordmem.data.model.pojo.Question
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import java.util.ArrayList
import javax.inject.Inject

class AnswerAdapter @Inject
constructor(
  val context: Context,
  private val question: Question,
  private val quizType: Int
) : RecyclerView.Adapter<ViewHolder>() {

  private var answerList: List<Answer> = question.answers!!
  private var isSelected = false
  private val options = ArrayList(listOf("A", "B", "C", "D", "E", "F"))

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    var view: View
    view = LayoutInflater.from(parent.context)
        .inflate(R.layout.answers_list_item, parent, false)
    when (viewType) {
      SettingsActivity.QUIZ_TYPE_WORD -> {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.answers_list_item, parent, false)
        return WordTypeViewHolder(view)
      }
      SettingsActivity.QUIZ_TYPE_SENTENCE -> {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.answers_list_item_2, parent, false)
        return SentenceTypeViewHolder(view)
      }
    }
    return WordTypeViewHolder(view)
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    val answer = answerList[position]
    val correct = answerList.singleOrNull { it.isCorrect }
    correct?.isCorrect = true
    when (quizType) {
      SettingsActivity.QUIZ_TYPE_WORD -> {
        (holder as WordTypeViewHolder).cardView.background = ContextCompat.getDrawable(
            context,
            R.drawable.answer_card_border
        )
        holder.title.text = answer.word!!.singleTurkish
        holder.cardView.setOnClickListener {
          if (!isSelected) {
            holder.title.setTextColor(
                ContextCompat.getColor(context, R.color.white)
            )
            if (answer.isCorrect) {
              question.isUserAnswerWasCorrect = true
              holder.cardView.background = ContextCompat.getDrawable(
                  context,
                  R.drawable.answer_card_correct_fill
              )
              (context as QuizActivity).nextQuestion(question, answer, true)
            } else {
              question.isUserAnswerWasCorrect = false
              holder.cardView.background = ContextCompat.getDrawable(
                  context,
                  R.drawable.answer_card_wrong_fill
              )
              holder.cardView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
              holder.cardView.animation = shake
              (context as QuizActivity).nextQuestion(question, answer, false)
            }
            isSelected = true
          }
        }
      }

      SettingsActivity.QUIZ_TYPE_SENTENCE -> {
        (holder as SentenceTypeViewHolder).option.text = options[position]
        holder.title.text = answer.word!!.meaning
        holder.cardView.setOnClickListener {
          if (!isSelected) {
            if (answer.isCorrect) {
              question.isUserAnswerWasCorrect = true
              holder.cardView.background = ContextCompat.getDrawable(
                  context,
                  R.drawable.answer_card_corrct_border
              )
              holder.title.setTextColor(
                  ContextCompat.getColor(context, R.color.primary)
              )
              holder.option.setTextColor(
                  ContextCompat.getColor(context, R.color.primary)
              )
              holder.divider.background = ContextCompat.getDrawable(context, R.color.primary)
              (context as QuizActivity).nextQuestion(question, answer, true)
            } else {
              question.isUserAnswerWasCorrect = false
              holder.cardView.background = ContextCompat.getDrawable(
                  context,
                  R.drawable.answer_card_wrong_border
              )
              holder.title.setTextColor(
                  ContextCompat.getColor(context, R.color.red)
              )
              holder.option.setTextColor(
                  ContextCompat.getColor(context, R.color.red)
              )
              holder.divider.background = ContextCompat.getDrawable(context, R.color.red)
              holder.cardView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
              holder.cardView.animation = shake
              (context as QuizActivity).nextQuestion(question, answer, false)
            }
            isSelected = true
          }
        }
      }
    }
  }

  override fun getItemCount(): Int {
    return answerList.size
  }

  inner class SentenceTypeViewHolder internal constructor(view: View) : ViewHolder(
      view
  ) {
    internal var title: TextView = view.findViewById<View>(R.id.test_item_word) as TextView
    internal var option: TextView = view.findViewById<View>(R.id.test_option) as TextView
    internal var cardView: CardView = view.findViewById<View>(R.id.answer_card_view) as CardView
    internal var divider: View = view.findViewById(R.id.divider)

  }

  class WordTypeViewHolder(view: View) : ViewHolder(view) {

    internal var title: TextView = view.findViewById<View>(R.id.test_item_word) as TextView
    internal var cardView: CardView = view.findViewById<View>(R.id.answer_card_view) as CardView

  }

  override fun getItemViewType(position: Int): Int {
    return quizType
  }
}
