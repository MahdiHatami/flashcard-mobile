package com.mutlak.metis.wordmem.data.model

import java.io.Serializable


open class Answer(
    var word: Word? = null,
    var isCorrect: Boolean = false,
    var showCorrect: Boolean = false
) : Serializable {
//  companion object {
//    @JvmField @Suppress("unused")
//    val CREATOR = createParcel { RedditNews(it) }
//  }
//
//  override fun writeToParcel(dest: Parcel?, flags: Int) {
//
//    dest?.writeByte((if (isCorrect) 1 else 0).toByte())
//    dest?.writeByte((if (showCorrect) 1 else 0).toByte())
//  }
//
//  override fun describeContents(): Int {
//    return 0
//  }
}
