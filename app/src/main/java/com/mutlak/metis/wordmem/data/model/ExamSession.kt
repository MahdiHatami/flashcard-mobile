package com.mutlak.metis.wordmem.data.model


data class ExamSession(
    var id: Int = 0,
    var questions: List<Question>? = null,
    var time: Int = 0
)
