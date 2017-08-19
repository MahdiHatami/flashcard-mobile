package com.mutlak.metis.wordmem

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.mutlak.metis.wordmem.injection.component.ApplicationComponent
import com.mutlak.metis.wordmem.injection.component.DaggerApplicationComponent
import com.mutlak.metis.wordmem.injection.module.ApplicationModule
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class MvpStarterApplication : MultiDexApplication() {

    internal var mApplicationComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()

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

        operator fun get(context: Context): MvpStarterApplication {
            return context.applicationContext as MvpStarterApplication
        }
    }
}
