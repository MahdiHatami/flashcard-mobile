package com.mutlak.metis.wordmem.features.intro

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.local.PreferencesHelper
import com.mutlak.metis.wordmem.features.landing.LandingActivity

class IntroActivity : AppIntro() {

  lateinit var mPref : PreferencesHelper

  companion object {
    const val SHOW_APP_INTRO = "show_app_intro"
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mPref = PreferencesHelper(this)

    val isShowed = mPref.getBoolean(SHOW_APP_INTRO, false)

    if (!isShowed) {

      setDoneText(getString(R.string.done))
      setSkipText(getString(R.string.skip))

      addSlide(AppIntroFragment.newInstance(getString(R.string.intro_review_title),
          getString(R.string.intro_review_content), R.drawable.ic_review_intro,
          ContextCompat.getColor(this, R.color.primary_dark)))

      addSlide(AppIntroFragment.newInstance(getString(R.string.intro_quiz_title),
          getString(R.string.intro_quiz_content), R.drawable.ic_quiz_intro,
          ContextCompat.getColor(this, R.color.primary_dark)))

      addSlide(AppIntroFragment.newInstance(getString(R.string.intro_settings_title),
          getString(R.string.intro_settings_content), R.drawable.ic_settings_intro,
          ContextCompat.getColor(this, R.color.primary_dark)))
    } else {
      redirectToLanding()
    }
  }

  override fun onSkipPressed(currentFragment: Fragment?) {
    super.onSkipPressed(currentFragment)
    redirectToLanding()
  }

  override fun onDonePressed(currentFragment: Fragment?) {
    super.onDonePressed(currentFragment)
    redirectToLanding()
  }

  override fun onBackPressed() {}

  private fun redirectToLanding() {
    mPref.putBoolean(SHOW_APP_INTRO, true)
    finish()
    startActivity(Intent(this@IntroActivity, LandingActivity::class.java))
  }
}
