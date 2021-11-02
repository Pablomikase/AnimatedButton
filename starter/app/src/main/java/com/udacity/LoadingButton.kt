package com.udacity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Property
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = ""
    private var buttonBackgroundColorBefore = 0
    private var buttonBackgroundColorAfter = 0
    var downloadingPercent = 0.1f




    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {

        isClickable = true
        downloadingPercent = 0.1f * width
        //Attributes and initial status of the button
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        buttonText = attributes.getString(R.styleable.LoadingButton_textStatusButton).toString()
        buttonBackgroundColorBefore =
            attributes.getColor(R.styleable.LoadingButton_starterBackgroundColorBefore, 0)
        buttonBackgroundColorAfter =
            attributes.getColor(R.styleable.LoadingButton_starterBackgroundColorAfter, 0)
        attributes.recycle()



    }


    private fun colorizer() {
        //var animator = ObjectAnimator.ofInt(this,"backgroundColor", Color.BLACK, Color.RED).start()
        var animator = ObjectAnimator.ofArgb(
            this,
            "backgroundColor",
            buttonBackgroundColorBefore,
            buttonBackgroundColorAfter
        )
        animator.duration = 1500
        animator.repeatCount = 0
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()


    }

    private fun scaler(){
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

        val animatorBar = ObjectAnimator.ofPropertyValuesHolder(
            this, scaleX)
        animatorBar.duration = 1500
        animatorBar.repeatCount = 0
        animatorBar.repeatMode = ObjectAnimator.REVERSE
        animatorBar.start()
    }


    private fun updateDownloadStatus(){
        downloadingPercent+=100
        val valueAnimator = ValueAnimator.ofFloat(0f, width.toFloat())
        valueAnimator.duration = 3000
        valueAnimator.start()
        valueAnimator.addUpdateListener(object:ValueAnimator.AnimatorUpdateListener{
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val animatedValue = animation?.animatedValue as Float
                this@LoadingButton.downloadingPercent = animatedValue
            }
        })

    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val backgroundPaint = Paint().apply {
        color = buttonBackgroundColorBefore
    }

    private val animatedBarPaint = Paint().apply {
        color = buttonBackgroundColorAfter
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Drawing Background
        val buttonRectangle = Rect(0,0,width,height)
        canvas?.drawRect(buttonRectangle, backgroundPaint)

        //Drawing bar status
        val downloadRectangle = Rect(0,0,downloadingPercent.toInt(), height)
        canvas?.drawRect(downloadRectangle, animatedBarPaint)

        //Drawing text
        val xTextPosition = width.div(2).toFloat()
        val yTextPosition = height.div(2).toFloat() - (paint.descent() + paint.ascent()) / 2
        canvas?.drawText(buttonText, xTextPosition, yTextPosition, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {

        //colorizer()
        //scaler()
        updateDownloadStatus()
        invalidate()
        if (super.performClick()) return true
        return true
    }
}