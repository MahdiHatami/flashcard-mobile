package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.base.BaseFragment
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.injection.module.ActivityModule
import com.mutlak.metis.wordmem.injection.module.FragmentModule
import dagger.Component

/**
 * A dagger component that will live during the lifecycle of an Activity or Fragment but it won't
 * be destroy during configuration changes. Check [BaseActivity] and [BaseFragment] to
 * see how this components survives configuration changes.
 * Use the [ConfigPersistent] scope to annotate dependencies that need to survive
 * configuration changes (for example Presenters).
 */
@ConfigPersistent
@Component(dependencies = [ApplicationComponent::class])
interface ConfigPersistentComponent {

  fun activityComponent(activityModule: ActivityModule): ActivityComponent

  fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent

}
