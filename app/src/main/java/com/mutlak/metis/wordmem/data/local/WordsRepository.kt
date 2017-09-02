package com.mutlak.metis.wordmem.data.local

import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.data.model.Word


interface WordsRepository {
  fun getReviewWords(reviewType: Int, limit: Int): List<Word>

  fun saveWords(wordList: List<Word>)

  fun updateWord(word: Word)

  val settings: Settings

  fun updateSettings(settings: Settings)

  val allWords: List<Word>

  val newWordsCount: Int

  val bookMarkWordsCount: Int

  val learntWordsCount: Int
}
