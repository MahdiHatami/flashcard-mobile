package com.mutlak.metis.wordmem.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager


object ViewUtil {

  fun pxToDp(px: Float): Float {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return px / (densityDpi / 160f)
  }

  fun dpToPx(dp: Int): Int {
    val density = Resources.getSystem().displayMetrics.density
    return Math.round(dp * density)
  }

  fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
  }

  fun getViewRadius(view: View): Float {
    return Math.hypot((view.getHeight() / 2).toDouble(), (view.getWidth() / 2).toDouble()).toFloat()
  }

}
