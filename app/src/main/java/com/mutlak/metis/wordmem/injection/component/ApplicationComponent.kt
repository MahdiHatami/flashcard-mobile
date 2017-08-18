package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.remote.MvpStarterService
import com.mutlak.metis.wordmem.injection.ApplicationContext
import com.mutlak.metis.wordmem.injection.module.ApplicationModule
import android.app.Application
import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun dataManager(): DataManager

    fun mvpBoilerplateService(): MvpStarterService
}
