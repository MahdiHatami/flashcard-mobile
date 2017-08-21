package com.mutlak.metis.wordmem.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.Date

open class Word(
    // You can put properties in the constructor as long as all of them are initialized with
    // default values. This ensures that an empty constructor is generated.
    // All properties are by default persisted.
    // Properties can be annotated with PrimaryKey or Index.
    // If you use non-nullable types, properties must be initialized with non-null values.
    @PrimaryKey var english: String = "",
    var id: Int = 0,
    var type: String? = null,
    var meaning: String? = null,
    var turkish: String? = null,
    var image: String? = null,
    var sentences: RealmList<Sentense>? = null,
    var ignored: Boolean = false,
    var learnt: Boolean = false,
    var bookmarked: Boolean = false,
    var totalReview: Int = 0,
    var totalWrongs: Int = 0,
    var totalCorrects: Int = 0,
    var lastSeen: Date? = null
) : Serializable, RealmObject() {
  val singleSentence: String
    get() {
      val s = ""
      if (sentences != null && sentences!!.size > 0) {
        val w = sentences!!.iterator().next()
        if (w != null)
          if (w.title == null) return ""
        return w.title!!
      }
      return s
    }

  val singleTurkish: String
    get() {
      if (turkish == null || turkish!!.isEmpty()) {
        return ""
      } else {
        val m = turkish!!.replace(";".toRegex(), ",").split(
            ",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (m.isNotEmpty()) {
          m[0]
        } else {
          ""
        }
      }
    }
}

