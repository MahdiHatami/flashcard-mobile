package com.mutlak.metis.wordmem.features.settings

import android.os.Bundle
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.base.BaseActivity

class SettingsActivity : BaseActivity() {

    companion object {
        const val TAG = "SettingsActivity"
        const val QUIZ_TYPE_WORD = 1
        const val QUIZ_TYPE_SENTENCE = 2
        const val ALARM_REQUEST_CODE = 11
        val reviewList = mutableListOf(10, 20, 25)
        val quizList = mutableListOf(5, 10, 15)
        val optionList = mutableListOf(4, 5, 6)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
    }

    override val layout: Int
        get() = R.layout.activity_settings
}
