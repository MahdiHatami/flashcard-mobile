package com.mutlak.metis.wordmem.data.model.pojo

import com.mutlak.metis.wordmem.data.model.Word
import java.io.Serializable

open class Answer(
    var word: Word? = null,
    var isCorrect: Boolean = false,
    var showCorrect: Boolean = false
) : Serializable
