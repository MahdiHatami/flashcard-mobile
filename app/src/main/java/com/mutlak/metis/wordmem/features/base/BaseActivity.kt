package com.mutlak.metis.wordmem.features.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.LongSparseArray
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.mutlak.metis.wordmem.MutlakApplication
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.injection.component.ActivityComponent
import com.mutlak.metis.wordmem.injection.component.ConfigPersistentComponent
import com.mutlak.metis.wordmem.injection.component.DaggerConfigPersistentComponent
import com.mutlak.metis.wordmem.injection.module.ActivityModule
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract activity that every other Activity in this application must implement. It provides the
 * following functionality:
 * - Handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent are kept across configuration changes.
 * - Set up and handles a GoogleApiClient instance that can be used to access the Google sign in
 * api.
 * - Handles signing out when an authentication error event is received.
 */
abstract class BaseActivity : AppCompatActivity() {

  private val TAG = BaseActivity.javaClass.simpleName
  private var mActivityComponent: ActivityComponent? = null
  private var mActivityId: Long = 0
  var mProgress: MaterialDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout)
    ButterKnife.bind(this)
    // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
    // being called after a configuration change.
    mActivityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
    val configPersistentComponent: ConfigPersistentComponent
    if (sComponentsArray.get(mActivityId) == null) {
      Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId)
      configPersistentComponent = DaggerConfigPersistentComponent.builder()
          .applicationComponent(MutlakApplication[this].component)
          .build()
      sComponentsArray.put(mActivityId, configPersistentComponent)
    } else {
      Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId)
      configPersistentComponent = sComponentsArray.get(mActivityId)
    }
    mActivityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
    mActivityComponent?.inject(this)
  }

  abstract val layout: Int

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putLong(KEY_ACTIVITY_ID, mActivityId)
  }

  override fun onDestroy() {
    if (!isChangingConfigurations) {
      Timber.i("Clearing ConfigPersistentComponent id=%d", mActivityId)
      sComponentsArray.remove(mActivityId)
    }
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  fun activityComponent(): ActivityComponent {
    return mActivityComponent as ActivityComponent
  }

  companion object {

    private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
    private val NEXT_ID = AtomicLong(0)
    private val sComponentsArray = LongSparseArray<ConfigPersistentComponent>()
  }

  fun showProgress() {
    mProgress = MaterialDialog.Builder(this).progress(true, 0)
        .widgetColorRes(R.color.primary)
        .cancelable(false)
        .show()
  }

  fun hideProgress() {
    try {
      Thread.sleep(TimeUnit.SECONDS.toMillis(1))
      mProgress?.dismiss()
    } catch (e: InterruptedException) {
      e.printStackTrace()
    }

    Log.d(TAG, "hideProgress: ")

  }

  override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
  }

  fun changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.statusBarColor = ContextCompat.getColor(this, color)
    }
  }

}
