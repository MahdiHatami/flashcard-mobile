package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.features.review.ReviewFragment
import com.mutlak.metis.wordmem.features.review.TakeQuizFragment
import com.mutlak.metis.wordmem.injection.PerFragment
import com.mutlak.metis.wordmem.injection.module.FragmentModule
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {

  fun inject(reviewFragment: ReviewFragment)

  fun inject(quizFragment: TakeQuizFragment)
}
