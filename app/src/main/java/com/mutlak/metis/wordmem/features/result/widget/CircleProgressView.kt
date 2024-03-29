package com.mutlak.metis.wordmem.features.result.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.mutlak.metis.wordmem.R

class CircleProgressView(
  context: Context,
  attrs: AttributeSet
) : View(context, attrs) {
  private var mProgress: Float = 0.toFloat()
  private var circleWidth: Float = 0.toFloat()
  private var backgroundStrokeWidth: Float = 0.toFloat()

  private var mCircleColor: Int = 0
  private var mBackgroundColor: Int = 0

  private var mTextColor: Int = 0

  private lateinit var mRectF: RectF
  private lateinit var mBackgroundPaint: Paint
  private lateinit var mCirclePaint: Paint
  var interpolator: Interpolator? = null

  private var mIsTextEnabled: Boolean = false

  private var mTextPrefix = ""
  private var startAngle: Float = 0.toFloat()

  private var mTextView: TextView? = null

  private var mTextSize: Int = 0
  private lateinit var mLayout: LinearLayout

  var progressAnimationListener: ProgressAnimationListener? = null
    private set

  init {
    init(context, attrs)
  }

  private fun init(
    context: Context,
    attrs: AttributeSet
  ) {
    mRectF = RectF()

    setDefaultValues()
    val typedArray = context.theme.obtainStyledAttributes(
        attrs, R.styleable.CircularProgressView,
        0, 0
    )

    try {
      mProgress = typedArray.getFloat(R.styleable.CircularProgressView_cpv_progress, mProgress)
      circleWidth = typedArray.getDimension(
          R.styleable.CircularProgressView_cpv_circle_width,
          circleWidth
      )
      backgroundStrokeWidth = typedArray.getDimension(
          R.styleable.CircularProgressView_cpv_background_circle_width,
          backgroundStrokeWidth
      )
      mCircleColor = typedArray.getInt(
          R.styleable.CircularProgressView_cpv_circle_color,
          mCircleColor
      )
      mBackgroundColor = typedArray.getInt(
          R.styleable.CircularProgressView_cpv_background_circle_color,
          mBackgroundColor
      )
      mTextColor = typedArray.getInt(R.styleable.CircularProgressView_cpv_text_color, mTextColor)
      mTextSize = typedArray.getInt(R.styleable.CircularProgressView_cpv_text_size, mTextSize)
      mTextPrefix = typedArray.getString(R.styleable.CircularProgressView_cpv_text_prefix)
          .toString()
    } finally {
      typedArray.recycle()
    }

    // Init Background
    mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    mBackgroundPaint.color = mBackgroundColor
    mBackgroundPaint.style = Paint.Style.STROKE
    mBackgroundPaint.strokeWidth = backgroundStrokeWidth

    // Init Circle
    mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    mCirclePaint.color = mCircleColor
    mCirclePaint.style = Paint.Style.STROKE
    mCirclePaint.strokeCap = Paint.Cap.ROUND
    mCirclePaint.strokeWidth = circleWidth

    // Init TextView
    mTextView = TextView(context)
    mTextView?.visibility = View.VISIBLE
    mTextView?.textSize = mTextSize.toFloat()
    mTextView?.setTextColor(mTextColor)

    // Init Layout
    mLayout = LinearLayout(context)
    mLayout.addView(mTextView)
    showTextView(mIsTextEnabled)
  }

  private fun showTextView(mIsTextEnabled: Boolean) {
    mTextView?.text = textPrefix + Math.round(mProgress).toString()
    mTextView?.visibility = if (mIsTextEnabled) View.VISIBLE else View.GONE
    mTextView?.visibility = if (mTextView?.text!!.isNotEmpty()) View.VISIBLE else View.GONE
    invalidate()
  }

  private fun setDefaultValues() {
    mProgress = 0f
    circleWidth = resources.getDimension(R.dimen.default_circle_width)
    backgroundStrokeWidth = resources.getDimension(R.dimen.default_circle_background_width)
    mCircleColor = Color.BLACK
    mTextColor = Color.BLACK
    mBackgroundColor = Color.GRAY
    startAngle = -90f
    mIsTextEnabled = true
    mTextPrefix = ""
    mTextSize = 20
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    // Draw Background Circle
    canvas.drawOval(mRectF, mBackgroundPaint)

    // Draw Circle
    val angle = 360 * mProgress / 100
    canvas.drawArc(mRectF, startAngle, angle, false, mCirclePaint)

    // Draw TextView
    mLayout.measure(width, height)
    mLayout.layout(0, 0, width, height)
    canvas.translate(
        (width / 2 - mTextView?.width!! / 2).toFloat(),
        (height / 2 - mTextView?.height!! / 2).toFloat()
    )
    mLayout.draw(canvas)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val height = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
    val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
    val min = Math.min(width, height)
    setMeasuredDimension(min, min)
    val stroke = if (circleWidth > backgroundStrokeWidth) circleWidth else backgroundStrokeWidth
    mRectF.set(0 + stroke / 2, 0 + stroke / 2, min - stroke / 2, min - stroke / 2)
  }

  fun setCirclerWidth(circleWidth: Float) {
    this.circleWidth = circleWidth
    mCirclePaint.strokeWidth = circleWidth
    requestLayout()
    invalidate()
  }

  var circleColor: Int
    get() = mCircleColor
    set(circleColor) {
      this.mCircleColor = circleColor
      mCirclePaint.color = circleColor
      invalidate()
    }

  var textPrefix: String
    get() = mTextPrefix
    set(textPrefix) {
      this.mTextPrefix = textPrefix
      showTextView(mIsTextEnabled)
    }

  var progress: Float
    get() = mProgress
    set(progress) {
      this.mProgress = if (progress <= 100) progress else 100F
      mTextView?.text = mTextPrefix + Math.round(mProgress).toString()
      showTextView(mIsTextEnabled)
      invalidate()

      if (progressAnimationListener != null) {
        progressAnimationListener?.onValueChanged(progress)
      }
    }

  var textSize: Int
    get() = mTextSize
    set(textSize) {
      this.mTextSize = textSize
      mTextView?.textSize = textSize.toFloat()
      invalidate()
    }

  var text: String
    get() = mTextView.toString()
    set(value) {
      this.mTextView?.text = text
      this.mTextView?.visibility = VISIBLE
      invalidate()
    }

  var isTextEnabled: Boolean
    get() = mIsTextEnabled
    set(isTextEnabled) {
      this.mIsTextEnabled = isTextEnabled
      showTextView(isTextEnabled)
    }

  var textColor: Int
    get() = mTextColor
    set(textColor) {
      this.mTextColor = textColor
      mTextView?.setTextColor(textColor)
      invalidate()
    }

  fun setProgressWithAnimation(
    progress: Float,
    duration: Int
  ) {
    val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
    objectAnimator.duration = duration.toLong()
    objectAnimator.interpolator =
      if (interpolator != null) interpolator else DecelerateInterpolator()
    objectAnimator.addListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {

      }

      override fun onAnimationEnd(animation: Animator) {
        mProgress = if (progress <= 100) progress else 100F
        if (progressAnimationListener != null) {
          progressAnimationListener?.onAnimationEnd()
        }
      }

      override fun onAnimationCancel(animation: Animator) {

      }

      override fun onAnimationRepeat(animation: Animator) {

      }
    })
    objectAnimator.addUpdateListener { animation ->
      mTextView?.text = mTextPrefix + Math.round(animation.animatedValue as Float).toString()
    }
    objectAnimator.start()

    if (progressAnimationListener != null) {
      progressAnimationListener?.onValueChanged(progress)
    }
  }

  fun addAnimationListener(progressAnimationListener: ProgressAnimationListener) {
    this.progressAnimationListener = progressAnimationListener
  }
}
