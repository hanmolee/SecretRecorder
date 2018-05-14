package hanmo.com.secretrecoder.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.support.animation.FlingAnimation
import android.support.animation.FloatValueHolder
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.util.DLog
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_menu.view.*

/**
 * Created by hanmo on 2018. 5. 3..
 */
class SwipeButton : RelativeLayout {

    private var gestureDetector: GestureDetector? = null
    private var animator: ValueAnimator? = null
    private var flingAnimation: FlingAnimation? = null
    private var triggered = false
    private var animationStarted = false
    private val progressSubject = PublishSubject.create<Float>()
    private val completeSubject = PublishSubject.create<String>()

    lateinit var overlayView: View
    lateinit var buttonBackground: View

    fun getProgressObservable(): Observable<Float> {
        return progressSubject.hide()
    }

    fun getCompleteObservable(): Observable<String> {
        return completeSubject.hide()
    }
    constructor(context: Context) : super(context) {
        init()
    }


    private fun init() {
        val view = View.inflate(context, R.layout.view_menu, this)
        overlayView = view.setting
        buttonBackground = view.settingButton



        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
                animateShakeButton()
                return true
            }

            override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
                cancelAnimations()
                setDragProgress(motionEvent1.x)
                return true
            }

            override fun onFling(downEvent: MotionEvent, moveEvent: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (velocityX < 0) {
                    return false
                }
                cancelAnimations()
                DLog.e("startValue : $velocityX")
                flingAnimation = FlingAnimation(FloatValueHolder(moveEvent.x))
                flingAnimation!!.setStartVelocity(velocityX)
                        .setMaxValue(width.toFloat())
                        .setFriction(FLING_FRICTION)
                        .addUpdateListener { dynamicAnimation, `val`, velocity -> setDragProgress(`val`) }
                        .addEndListener { dynamicAnimation, canceled, `val`, velocity -> onDragFinished(`val`) }
                        .start()

                return true
            }
        })
        gestureDetector!!.setIsLongpressEnabled(false)
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (triggered) {
            return true
        }
        if (gestureDetector!!.onTouchEvent(event)) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> onDragFinished(event.x)
        }

        return true
    }

    private fun setDragProgress(xx: Float) {
        var x = -xx
        x += buttonBackground.width
        /*if (!animationStarted) {
            x += buttonBackground.width
        } else {
            x = -x - buttonBackground.width
        }*/
        progressSubject.onNext(x / width)
        val translation = calculateTranslation(x)
        setPadding(-translation, 0, translation, 0)
        if (!triggered) {
            overlayView.alpha = (x + buttonBackground.width) / width
            overlayView.layoutParams.width = Math.min(x + overlayView.width + translation.toFloat(), buttonBackground.width.toFloat()).toInt()
            overlayView.requestLayout()

            buttonBackground.alpha = 1 - overlayView.alpha

            DLog.e("00000000000 width : $width")
            DLog.e("00000000000 x : $x")
            DLog.e("00000000000 alpha! : ${overlayView.alpha}")
            DLog.e("00000000000 overlayWidth : ${overlayView.width}")
            DLog.e("00000000000 translation : $translation")
            DLog.e("00000000000 overlayView.x ${overlayView.x}")

            DLog.e("00000000000 param width : ${Math.min(x + overlayView.width + translation.toFloat(), buttonBackground.width.toFloat()).toInt()}")



        } else {
            overlayView.alpha = 0f
            overlayView.layoutParams.width = 0
            overlayView.requestLayout()

            buttonBackground.alpha = 1f
        }
    }

    private fun calculateTranslation(x: Float): Int {
        return x.toInt() / 25
    }

    private fun cancelAnimations() {
        if (animator != null) {
            animator?.cancel()
        }
        if (flingAnimation != null) {
            flingAnimation?.cancel()
        }
    }

    private fun onDragFinished(finalX: Float) {
        if (finalX < 0) {
            animateToEnd(finalX)
        } else {
            animateToStart()
        }
    }

    private fun animateToStart() {
        cancelAnimations()
        DLog.e("START")
        animationStarted = true
        animator = ValueAnimator.ofFloat(overlayView.width.toFloat(), 0f)
        with(animator!!) {
            addUpdateListener { valueAnimator ->
                setDragProgress(valueAnimator.animatedValue as Float)
                DLog.e("aa : ${valueAnimator.animatedValue}")}
            duration = ANIMATE_TO_START_DURATION.toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (triggered) {

                    }
                }
            })
            start()
        }
    }

    private fun animateToEnd(currentValue: Float) {
        cancelAnimations()
        DLog.e("END")
        var rightEdge = buttonBackground.width + buttonBackground.x
        rightEdge += calculateTranslation(rightEdge).toFloat()
        animator = ValueAnimator.ofFloat(currentValue, rightEdge)
        with(animator!!) {
            addUpdateListener { valueAnimator -> setDragProgress(valueAnimator.animatedValue as Float) }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    triggered = true
                    completeSubject.onNext("start")
                }
            })
            duration = ANIMATE_TO_END_DURATION.toLong()
            start()
        }
    }

    private fun animateShakeButton() {
        cancelAnimations()
        var rightEdge = buttonBackground.width + buttonBackground.x
        rightEdge += calculateTranslation(rightEdge).toFloat()
        animator = ValueAnimator.ofFloat(0f, rightEdge, 0f, rightEdge / 2, 0f, rightEdge / 4, 0f)
        with(animator!!) {
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                val translation = calculateTranslation(valueAnimator.animatedValue as Float)
                setPadding(translation, 0, -translation, 0)
            }
            duration = ANIMATE_SHAKE_DURATION.toLong()
            start()
        }

    }

    companion object {

        val THRESHOLD_FRACTION = .85
        val ANIMATE_TO_START_DURATION = 200
        val ANIMATE_TO_END_DURATION = 50
        val ANIMATE_SHAKE_DURATION = 2000
        val FLING_FRICTION = .85f
    }
}
