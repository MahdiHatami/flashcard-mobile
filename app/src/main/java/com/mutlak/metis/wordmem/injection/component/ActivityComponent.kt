package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.features.detail.*
import com.mutlak.metis.wordmem.features.insertWord.*
import com.mutlak.metis.wordmem.features.landing.*
import com.mutlak.metis.wordmem.features.quiz.*
import com.mutlak.metis.wordmem.features.result.*
import com.mutlak.metis.wordmem.features.review.*
import com.mutlak.metis.wordmem.features.settings.*
import com.mutlak.metis.wordmem.injection.*
import com.mutlak.metis.wordmem.injection.module.*
import dagger.*

@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
  fun inject(baseActivity: BaseActivity)

  fun inject(detailActivity: DetailActivity)

  fun inject(settingsActivity: SettingsActivity)

  fun inject(reviewActivity: ReviewActivity)

  fun inject(quizActivity: QuizActivity)

  fun inject(resultActivity: ResultActivity)

  fun inject(landingActivity: LandingActivity)

  fun inject(newWordActivity: NewWordActivity)
}
