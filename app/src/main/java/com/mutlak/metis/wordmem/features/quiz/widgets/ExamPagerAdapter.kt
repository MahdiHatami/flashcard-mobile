package com.mutlak.metis.wordmem.features.quiz.widgets

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.google.gson.Gson
import com.mutlak.metis.wordmem.data.model.ExamSession
import com.mutlak.metis.wordmem.features.quiz.QuestionFragment


open class ExamPagerAdapter(val session: ExamSession,
    fm: FragmentManager) : FragmentStatePagerAdapter(
    fm) {

  override fun getItem(position: Int): android.support.v4.app.Fragment {
    val currentQuestion = session.questions!![position]
    val s = Gson().toJson(currentQuestion)
    return QuestionFragment.create(s, session.id)
  }

  override fun getCount(): Int {
    return session.questions!!.size
  }

}
