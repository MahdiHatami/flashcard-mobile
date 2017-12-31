package com.mutlak.metis.wordmem.common

import com.mutlak.metis.wordmem.data.model.Word

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
object TestDataFactory {

  fun makeWordList(count: Int): List<Word> {
    return (0 until count).map { makeWord() }
  }

  fun makeListRibots(number: Int): List<Word> {
    val words = arrayListOf<Word>()
    var i = 0
    while (i < number) {
      words.add(makeWord())
      i++
    }
    return words
  }

  private fun makeWord(): Word {
    return Word(english = "abandon",
        meaning = "to leave someone, especially someone you are responsible for",
        turkish = "terk etmek", type = "v")
  }
}
