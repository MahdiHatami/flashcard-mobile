package com.mutlak.metis.wordmem.data.model.pojo

import com.mutlak.metis.wordmem.data.model.Word
import java.io.Serializable


data class Question(
    var question: Word,
    var answers: List<Answer>? = null,
    var isUserAnswerWasCorrect: Boolean = false,
    var userResponse: Word? = null
) : Serializable
