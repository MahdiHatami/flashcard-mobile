package com.mutlak.metis.wordmem.injection.module

import android.app.*
import android.content.*
import com.mutlak.metis.wordmem.injection.*
import dagger.*

@Module
class ActivityModule(private val mActivity: Activity) {

  @Provides
  internal fun provideActivity(): Activity {
    return mActivity
  }

  @Provides
  @ActivityContext
  internal fun providesContext(): Context {
    return mActivity
  }
}
