package com.mutlak.metis.wordmem.injection.component

import android.app.Application
import android.content.Context
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.local.PreferencesHelper
import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.remote.MutlakService
import com.mutlak.metis.wordmem.injection.ApplicationContext
import com.mutlak.metis.wordmem.injection.module.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

  @ApplicationContext
  fun context(): Context

  fun application(): Application

  fun dataManager(): DataManager

  fun mutlakService(): MutlakService

  fun wordRepository(): WordsRepositoryImpl

  fun preferencesHelper(): PreferencesHelper
}
