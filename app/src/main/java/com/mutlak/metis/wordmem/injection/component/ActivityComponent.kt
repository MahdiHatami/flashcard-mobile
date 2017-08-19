package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.detail.DetailActivity
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.main.MainActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.review.ReviewActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import com.mutlak.metis.wordmem.injection.PerActivity
import com.mutlak.metis.wordmem.injection.module.ActivityModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(detailActivity: DetailActivity)

    fun inject(landingActivity: LandingActivity)

    fun inject(settingsActivity: SettingsActivity)

    fun inject(reviewActivity: ReviewActivity)

    fun inject(quizActivity: QuizActivity)
}
