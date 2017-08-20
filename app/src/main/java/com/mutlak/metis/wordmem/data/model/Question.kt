package com.mutlak.metis.wordmem.data.model


data class Question(
    var question: Word,
    var answers: List<Answer>? = null,
    var isUserAnswerWasCorrect: Boolean = false,
    var userResponse: Word? = null
)
