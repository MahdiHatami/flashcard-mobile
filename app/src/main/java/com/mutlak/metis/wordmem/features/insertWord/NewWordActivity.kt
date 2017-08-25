package com.mutlak.metis.wordmem.features.insertWord

import android.os.Bundle
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.base.BaseActivity

class NewWordActivity : BaseActivity() {

  override val layout: Int
    get() = R.layout.activity_new_word

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_word)

    changeStatusBarColor(R.color.landing_plus)

  }
}
