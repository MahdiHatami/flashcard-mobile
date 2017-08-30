package com.mutlak.metis.wordmem.extension

import android.animation.*
import android.os.*
import android.text.*
import android.view.*
import android.webkit.*
import android.widget.*
import timber.log.*

fun View.showIf(show: Boolean) {
  if (show) {
    show()
  } else {
    hide()
  }
}

fun View.show() {
  this.visibility = View.VISIBLE
}

fun View.hide() {
  this.visibility = View.GONE
}

inline fun WebView.setProgressChangedListener(crossinline onProgressChanged: (Int) -> Unit) {
  this.webChromeClient = object : WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
      onProgressChanged.invoke(newProgress)
    }
  }
}


fun View.circularReveal(backgroundColor: Int) {
  val showAndSetBackgroundColorFunction = {
    this.setBackgroundColor(backgroundColor)
    this.visibility = View.VISIBLE
  }

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    this.post {
      val cx = this.width / 2
      val cy = this.height / 2
      val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

      try {
        val animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
        animator.startDelay = 50
        animator.addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator?) {
            showAndSetBackgroundColorFunction.invoke()
          }
        })
        animator.start()
      } catch (e: Exception) {
        Timber.e(e, "Unable to perform circular reveal")
      }
    }
  } else {
    showAndSetBackgroundColorFunction.invoke()
  }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
      afterTextChanged.invoke(editable.toString())
    }
  })
}
