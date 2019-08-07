package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.insertWord.NewWordActivity
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.quiz.QuizActivity
import com.mutlak.metis.wordmem.features.result.ResultActivity
import com.mutlak.metis.wordmem.features.review.ReviewActivity
import com.mutlak.metis.wordmem.features.settings.SettingsActivity
import com.mutlak.metis.wordmem.injection.PerActivity
import com.mutlak.metis.wordmem.injection.module.ActivityModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
  fun inject(baseActivity: BaseActivity)

  fun inject(settingsActivity: SettingsActivity)

  fun inject(reviewActivity: ReviewActivity)

  fun inject(quizActivity: QuizActivity)

  fun inject(resultActivity: ResultActivity)

  fun inject(landingActivity: LandingActivity)

  fun inject(newWordActivity: NewWordActivity)
}
