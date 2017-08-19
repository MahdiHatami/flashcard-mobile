package com.mutlak.metis.wordmem.features.review

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.util.AlarmReciever
import com.mutlak.metis.wordmem.util.NotificationUtils

class ReviewActivity : BaseActivity() {

  private val mNotificationUtils: NotificationUtils = NotificationUtils(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mNotificationUtils.manager.cancel(AlarmReciever.REVIEW_NOTIFICAION_ID)
    } else {
      val mNotificationManager = getSystemService(
          Context.NOTIFICATION_SERVICE) as NotificationManager
      mNotificationManager.cancel(AlarmReciever.REVIEW_NOTIFICAION_ID)
    }

    if (intent.hasExtra(LandingActivity.REVIEW_TYPE)) {
      val ft = supportFragmentManager.beginTransaction()
      ft.replace(R.id.review_placeholder, ReviewFragment.newInstance())
      ft.commit()
//      var reviewFragment: ReviewFragment? = supportFragmentManager.findFragmentById(
//          R.id.review_placeholder) as ReviewFragment?
//      if (reviewFragment == null) {
//        reviewFragment = ReviewFragment.newInstance()
//        ActivityUtils.addFragmentToActivity(supportFragmentManager, reviewFragment,
//            R.id.review_placeholder)
//    }
    } else {
      redirectToLanding()
    }

  }

  override val layout: Int
    get() = R.layout.activity_review

  private fun redirectToLanding() {
    startActivity(Intent(this@ReviewActivity, LandingActivity::class.java))
  }

  override fun onBackPressed() {
    redirectToLanding()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_review, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        redirectToLanding()
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }
}

