package com.mutlak.metis.wordmem.features.review

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.NativeExpressAdView
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import io.realm.Realm
import java.util.*


class TakeQuizFragment : Fragment() {

    private var settings: Settings? = null

    @BindView(R.id.result_explore_subtitle_textview) lateinit var mExploreText: TextView
    @BindView(R.id.result_quiz_subtitle_textview) lateinit var mQuizText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm1 ->
            settings = realm.where(Settings::class.java).findFirst()
            settings?.quizLimit = settings?.reviewLimit!!
            if (settings?.quizType == 0) settings!!.quizType = SettingsActivity.QUIZ_TYPE_WORD
        }
        settings = realm.copyFromRealm(settings)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_take_quiz, container, false) as ViewGroup
        ButterKnife.bind(this, rootView)

        mExploreText.text = String.format(Locale.getDefault(), "%d %s", settings!!.reviewLimit,
                                          getString(R.string.words))
        mQuizText.text = String.format(Locale.getDefault(), "%d %s", settings!!.quizLimit,
                                       getString(R.string.words))

        val adView = rootView.findViewById<View>(R.id.native_ad) as NativeExpressAdView
        val request = AdRequest.Builder().build()
        adView.loadAd(request)

        return rootView
    }

    @OnClick(R.id.result_home_linear)
    fun homeOnClick() {
        activity.finish()
        startActivity(Intent(activity, LandingActivity::class.java))
    }

    @OnClick(R.id.result_quiz_linear)
    fun quizOnclick() {
        val i = Intent(activity, QuizActivity::class.java)
        startActivity(i)
    }

    @OnClick(R.id.result_explore_linear)
    fun exploreOnclick() {
        val intent = Intent(activity, ReviewActivity::class.java)
        intent.putExtra(LandingActivity.REVIEW_TYPE, LandingActivity.REVIEW_TYPE_NEW)
        startActivity(intent)
    }

    companion object {

        fun create(): TakeQuizFragment {
            return TakeQuizFragment()
        }
    }
}
