package com.mutlak.metis.wordmem.features.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mutlak.metis.wordmem.data.local.PreferencesHelper
import com.mutlak.metis.wordmem.features.intro.IntroActivity
import com.mutlak.metis.wordmem.features.landing.LanActivity

class MainActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)

    val mPref = PreferencesHelper(this)

    val isShowed = mPref.getBoolean(IntroActivity.SHOW_APP_INTRO, false)
    if (isShowed) {
      startActivity(Intent(this, LanActivity::class.java))
    } else {
      startActivity(Intent(this, IntroActivity::class.java))
    }
  }


}
