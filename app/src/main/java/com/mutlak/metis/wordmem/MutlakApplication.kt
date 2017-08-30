package com.mutlak.metis.wordmem

import android.content.*
import android.support.multidex.*
import com.crashlytics.android.*
import com.facebook.stetho.*
import com.mutlak.metis.wordmem.injection.component.*
import com.mutlak.metis.wordmem.injection.module.*
import com.squareup.leakcanary.*
import io.fabric.sdk.android.*
import io.realm.*
import timber.log.*
import uk.co.chrisjenx.calligraphy.*

class MutlakApplication : MultiDexApplication() {

  internal var mApplicationComponent: ApplicationComponent? = null

  override fun onCreate() {
    super.onCreate()

    Fabric.with(this, Crashlytics())

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      Stetho.initializeWithDefaults(this)
      LeakCanary.install(this)
    }
    CalligraphyConfig.initDefault(
        CalligraphyConfig.Builder().setDefaultFontPath("fonts/Ubuntu-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build())

    Realm.init(this)
    val realmConfiguration =
        RealmConfiguration.Builder().name("wordmem.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
    Realm.setDefaultConfiguration(realmConfiguration)
  }

  // Needed to replace the component with a test specific one
  var component: ApplicationComponent
    get() {
      if (mApplicationComponent == null) {
        mApplicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
      }
      return mApplicationComponent as ApplicationComponent
    }
    set(applicationComponent) {
      mApplicationComponent = applicationComponent
    }

  companion object {

    operator fun get(context: Context): MutlakApplication {
      return context.applicationContext as MutlakApplication
    }
  }
}
