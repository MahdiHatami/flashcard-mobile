package com.mutlak.metis.wordmem.features.main

import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.mutlak.metis.wordmem.data.local.*
import com.mutlak.metis.wordmem.features.intro.*
import com.mutlak.metis.wordmem.features.landing.*

class MainActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)

    val mPref = PreferencesHelper(this)

    val isShowed = mPref.getBoolean(IntroActivity.SHOW_APP_INTRO, false)
    if (isShowed) {
      startActivity(Intent(this, LandingActivity::class.java))
    } else {
      startActivity(Intent(this, IntroActivity::class.java))
    }
  }


}
