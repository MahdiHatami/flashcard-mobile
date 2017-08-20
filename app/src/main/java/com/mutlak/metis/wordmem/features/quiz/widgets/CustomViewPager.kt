package com.mutlak.metis.wordmem.features.quiz.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Interpolator
import com.github.paolorotolo.appintro.ScrollerCustomDuration


class CustomViewPager : ViewPager {

  constructor(context: Context) : super(context)
  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

  private var direction: SwipeDirection

  init {
    this.direction = SwipeDirection.all
    postInitViewPager()
  }


  private val TAG = CustomViewPager::class.java.simpleName
  private var initialXValue: Float = 0.toFloat()
  private var mScroller: ScrollerCustomDuration? = null

  private fun postInitViewPager() {
    try {
      val scroller = ViewPager::class.java.getDeclaredField("mScroller")
      scroller.isAccessible = true
      val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
      interpolator.isAccessible = true

      mScroller = ScrollerCustomDuration(context, interpolator.get(null) as Interpolator)
      scroller.set(this, mScroller)
    } catch (ignored: Exception) {
    }

  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    return if (this.isSwipeAllowed(event)) {
      super.onTouchEvent(event)
    } else false

  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    return if (this.isSwipeAllowed(event)) {
      super.onInterceptTouchEvent(event)
    } else false

  }

  private fun isSwipeAllowed(event: MotionEvent): Boolean {
    if (this.direction === SwipeDirection.all) return true

    if (direction === SwipeDirection.none)
    //disable any swipe
    {
      return false
    }

    if (event.action == MotionEvent.ACTION_DOWN) {
      initialXValue = event.x
      return true
    }

    if (event.action == MotionEvent.ACTION_MOVE) {
      try {
        val diffX = event.x - initialXValue
        if (diffX > 0 && direction === SwipeDirection.right) {
          // swipe from left to right detected
          return false
        } else if (diffX < 0 && direction === SwipeDirection.left) {
          // swipe from right to left detected
          return false
        }
      } catch (exception: Exception) {
        Log.e(TAG, "isSwipeAllowed: ", exception)
      }

    }

    return true
  }

  fun setAllowedSwipeDirection(direction: SwipeDirection) {
    this.direction = direction
  }

  fun setScrollDurationFactor(scrollFactor: Double) {
    mScroller!!.setScrollDurationFactor(scrollFactor)
  }

  enum class SwipeDirection {
    all, left, right, none
  }

}
