package com.mutlak.metis.wordmem.features.review

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import com.mutlak.metis.wordmem.*
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.features.landing.*
import com.mutlak.metis.wordmem.util.*

class ReviewActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)

    val mNotificationUtils = NotificationUtils(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mNotificationUtils.manager.cancel(AlarmReciever.REVIEW_NOTIFICAION_ID)
    } else {
      val mNotificationManager = getSystemService(
          Context.NOTIFICATION_SERVICE) as NotificationManager
      mNotificationManager.cancel(AlarmReciever.REVIEW_NOTIFICAION_ID)
    }

    changeStatusBarColor(R.color.landing_background_darker)

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

