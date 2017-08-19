package com.mutlak.metis.wordmem.features.review

import android.os.Bundle
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.base.BaseActivity

class ReviewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
    }

    override val layout: Int
        get() = R.layout.activity_review
}
