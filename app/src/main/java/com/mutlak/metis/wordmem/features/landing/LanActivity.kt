package com.mutlak.metis.wordmem.features.landing

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import butterknife.BindView
import butterknife.ButterKnife
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.result.widget.CircleProgressView


class LanActivity : AppCompatActivity() {

  @BindView(R.id.circle_progress_view) lateinit var mCircleProgressView: CircleProgressView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lan)
    ButterKnife.bind(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val window = window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = ContextCompat.getColor(this, R.color.landing_header_center)
    }

    mCircleProgressView.isTextEnabled = false
    mCircleProgressView.interpolator = AccelerateDecelerateInterpolator()
    mCircleProgressView.text = 233.toString()
    mCircleProgressView.setProgressWithAnimation(60F, 1000)
  }


}
