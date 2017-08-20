package com.mutlak.metis.wordmem.features.quiz.widgets

import android.content.Context
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Answer
import com.mutlak.metis.wordmem.data.model.Question
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import java.util.ArrayList
import java.util.Arrays
import javax.inject.Inject


class AnswerAdapter @Inject
constructor(val context: Context, private val question: Question,
    private val quizType: Int) : RecyclerView.Adapter<ViewHolder>() {


  private var answerList: List<Answer> = question.answers!!
  private var isSelected = false
  private val options = ArrayList(Arrays.asList("A", "B", "C", "D", "E", "F"))

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
    val view: View
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
    return null
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val answer = answerList[position]
    val correct = answerList.singleOrNull { it.isCorrect }
    correct?.isCorrect = true
    when (quizType) {
      SettingsActivity.QUIZ_TYPE_WORD -> {
        (holder as WordTypeViewHolder).cardView.background = ContextCompat.getDrawable(context,
            R.drawable.answer_card_border)
        holder.title.text = answer.word!!.singleTurkish
        holder.cardView.setOnClickListener { v ->
          if (!isSelected) {
            holder.title.setTextColor(
                ContextCompat.getColor(context, R.color.white))
            if (answer.isCorrect) {
              question.isUserAnswerWasCorrect= true
              holder.cardView.background = ContextCompat.getDrawable(context,
                  R.drawable.answer_card_correct_fill)
              (context as QuizActivity).nextQuestion(question, answer, true)
            } else {
              question.isUserAnswerWasCorrect = false
              holder.cardView.background = ContextCompat.getDrawable(context,
                  R.drawable.answer_card_wrong_fill)
              vibrate()
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
        holder.cardView.setOnClickListener { _ ->
          if (!isSelected) {
            if (answer.isCorrect) {
              question.isUserAnswerWasCorrect = true
              holder.cardView.background = ContextCompat.getDrawable(context,
                  R.drawable.answer_card_corrct_border)
              holder.title.setTextColor(
                  ContextCompat.getColor(context, R.color.primary))
              holder.option.setTextColor(
                  ContextCompat.getColor(context, R.color.primary))
              holder.divider.background = ContextCompat.getDrawable(context, R.color.primary)
              (context as QuizActivity).nextQuestion(question, answer, true)
            } else {
              question.isUserAnswerWasCorrect = false
              holder.cardView.background = ContextCompat.getDrawable(context,
                  R.drawable.answer_card_wrong_border)
              holder.title.setTextColor(
                  ContextCompat.getColor(context, R.color.red))
              holder.option.setTextColor(
                  ContextCompat.getColor(context, R.color.red))
              holder.divider.background = ContextCompat.getDrawable(context, R.color.red)

              vibrate()
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

  private fun vibrate() {
    val vibe = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibe.vibrate(60)
  }

  override fun getItemCount(): Int {
    return answerList.size
  }

  inner class SentenceTypeViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(
      view) {
    internal var title: TextView
    internal var option: TextView
    internal var cardView: CardView
    internal var divider: View

    init {
      option = view.findViewById<View>(R.id.test_option) as TextView
      title = view.findViewById<View>(R.id.test_item_word) as TextView
      cardView = view.findViewById<View>(R.id.answer_card_view) as CardView
      divider = view.findViewById(R.id.divider)
    }
  }

  class WordTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    internal var title: TextView
    internal var cardView: CardView

    init {

      this.title = view.findViewById<View>(R.id.test_item_word) as TextView
      cardView = view.findViewById<View>(R.id.answer_card_view) as CardView
    }
  }

  override fun getItemViewType(position: Int): Int {
    return quizType
  }
}
