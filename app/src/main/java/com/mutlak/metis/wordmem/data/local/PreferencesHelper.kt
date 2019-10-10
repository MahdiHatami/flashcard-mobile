package com.mutlak.metis.wordmem.data.local

import android.content.Context
import android.content.SharedPreferences
import com.mutlak.metis.wordmem.injection.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PreferencesHelper @Inject
constructor(@ApplicationContext context: Context) {

  private val mPref: SharedPreferences

  init {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
  }

  fun clear() {
    mPref.edit().clear().apply()
  }

  companion object {
    val PREF_FILE_NAME = "mutlak_memo_pref_file"
  }

  fun putString(key: String, value: String) {
    val editor = mPref.edit()
    editor.putString(key, value)
    editor.apply()
  }

  fun getString(key: String, defValue: String): String? {
    return mPref.getString(key, defValue)
  }

  fun putBoolean(key: String, value: Boolean) {
    val editor = mPref.edit()
    editor.putBoolean(key, value)
    editor.apply()
  }

  fun getBoolean(key: String, defValue: Boolean): Boolean {
    return mPref.getBoolean(key, defValue)
  }
}
