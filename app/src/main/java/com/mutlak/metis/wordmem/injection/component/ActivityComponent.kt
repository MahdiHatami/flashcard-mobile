package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.injection.PerActivity
import com.mutlak.metis.wordmem.injection.module.ActivityModule
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.features.detail.DetailActivity
import com.mutlak.metis.wordmem.features.main.MainActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(detailActivity: DetailActivity)
}
