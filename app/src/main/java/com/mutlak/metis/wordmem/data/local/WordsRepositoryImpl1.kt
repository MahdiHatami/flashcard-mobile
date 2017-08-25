package com.mutlak.metis.wordmem.data.local

import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.features.landing.LanActivity
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.internal.IOException
import java.io.Closeable
import java.util.ArrayList
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordsRepositoryImpl @Inject
constructor() : WordsRepository, Closeable {

  var realm: Realm = Realm.getDefaultInstance()

  override fun getReviewWords(reviewType: Int, limit: Int): List<Word> {
    var limit = limit
    var result: RealmResults<Word>? = null

    when (reviewType) {
      LanActivity.REVIEW_TYPE_NEW -> result = realm.where(Word::class.java)
          .equalTo(IGNORE, false)
          .notEqualTo(BOOKMARK, true)
          .notEqualTo(LEARNT, true)
          .findAllSorted(LAST_SEEN, Sort.ASCENDING)
      LanActivity.REVIEW_TYPE_BOOKMARK -> result = realm.where(Word::class.java)
          .equalTo(IGNORE, false)
          .equalTo(BOOKMARK, true)
          .findAllSorted(LAST_SEEN, Sort.ASCENDING)
      LanActivity.REVIEW_TYPE_LEARNT -> result = realm.where(Word::class.java)
          .equalTo(IGNORE, false)
          .equalTo(LEARNT, true)
          .findAllSorted(LAST_SEEN, Sort.ASCENDING)
    }
    val list = realm.copyFromRealm(result)
    if (reviewType != LanActivity.REVIEW_TYPE_NEW) {
      limit = list.size
    }
    val temp = randomize(limit, list)
    //        list = Stream.of(list).limit(limit).collect(Collectors.toList());
    Collections.shuffle(temp)
    return temp
  }

  override val allWords: List<Word>
    get() {
      val result = realm.where(Word::class.java).findAll()
      return realm.copyFromRealm(result)
    }

  override val newWordsCount: Int
    get() {
      val result = realm.where(Word::class.java)
          .equalTo(IGNORE, false)
          .notEqualTo(BOOKMARK, true)
          .notEqualTo(LEARNT, true)
          .count()
      return result.toInt()
    }

  override val bookMarkWordsCount: Int
    get() {
      val result = realm.where(Word::class.java).equalTo(IGNORE, false).equalTo(BOOKMARK,
          true).count()
      return result.toInt()
    }

  override val learntWordsCount: Int
    get() {
      val result = realm.where(Word::class.java).equalTo(IGNORE, false).equalTo(LEARNT,
          true).count()
      return result.toInt()
    }

  private fun randomize(limit: Int, list: MutableList<Word>): MutableList<Word> {
    val temp = ArrayList<Word>()
    var i = 0
    while (i < limit && list.size > 0) {
      val a = (Math.random() * list.size).toInt()
      temp.add(list[a])
      list.removeAt(a)
      i++
    }
    return temp
  }

  fun getQuizWords(limit: Int): List<Word> {
    val result = realm.where(Word::class.java).findAllSorted(LAST_SEEN, Sort.DESCENDING)
    var list = realm.copyFromRealm(result)
    list = list.take(limit)
    list = randomize(limit, list)
    Collections.shuffle(list)
    return list
  }

  override fun saveWords(wordList: List<Word>) {
    realm.executeTransaction { realm1 -> realm1.insert(wordList) }
  }

  override fun updateSettings(settings: Settings) {
    realm.executeTransaction { realm1 -> realm1.insertOrUpdate(settings) }
  }

  override fun updateWord(word: Word) {
    realm.executeTransaction { realm1 -> realm1.insertOrUpdate(word) }
  }

  override val settings: Settings
    get() {
      val settings = realm.where(Settings::class.java).equalTo("id", 1).findFirst()
      if (settings == null) {
        val s = Settings()
        s.reviewLimit = 10
        return s
      }
      return realm.copyFromRealm(settings)
    }

  @Throws(IOException::class)
  override fun close() {
    realm.close()
  }

  companion object {

    private val IGNORE = "ignored"
    private val BOOKMARK = "bookmarked"
    private val LEARNT = "learnt"
    private val LAST_SEEN = "lastSeen"
  }
}
