package com.mutlak.metis.wordmem.features.quiz

import com.mutlak.metis.wordmem.data.model.pojo.ExamSession
import com.mutlak.metis.wordmem.features.base.BaseView


interface QuizView : BaseView {

  fun initQuestionPager(session: ExamSession)
  fun redirectBack()
}

