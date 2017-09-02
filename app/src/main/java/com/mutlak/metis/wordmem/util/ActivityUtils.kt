package com.mutlak.metis.wordmem.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.reactivex.annotations.NonNull


object ActivityUtils {

  /**
   * The `fragment` is added to the container view with id `frameId`. The operation is
   * performed by the `fragmentManager`.
   */
  fun addFragmentToActivity(@NonNull fragmentManager: FragmentManager,
      @NonNull fragment: Fragment, frameId: Int) {
    checkNotNull(fragmentManager)
    checkNotNull(fragment)
    val transaction = fragmentManager.beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commit()
  }
}
