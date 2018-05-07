package hanmo.com.secretrecoder.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.animation.FlingAnimation
import android.support.animation.FloatValueHolder
import android.support.annotation.AttrRes
import android.support.annotation.Nullable
import android.support.annotation.StyleRes
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
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
                DLog.e("hanmo onScroll ! motionEvent : $motionEvent motionEvent1 : $motionEvent1 ")
                cancelAnimations()
                setDragProgress(motionEvent1.x)
                return true
            }

            override fun onFling(downEvent: MotionEvent, moveEvent: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                DLog.e("hanmo onFling ! downEvnet : $downEvent moveEvent : $moveEvent velocitiyX : $velocityX")

                if (velocityX < 0) {
                    return false
                }
                cancelAnimations()
                flingAnimation = FlingAnimation(FloatValueHolder(moveEvent.x))
                flingAnimation!!.setStartVelocity(velocityX)
                        .setMaxValue(buttonBackground.width.toFloat())
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

    private fun setDragProgress(x: Float) {
        progressSubject.onNext(x / buttonBackground.width)
        val translation = calculateTranslation(x)
        setPadding(-translation, 0, +translation, 0)
        if (!triggered) {
            overlayView.alpha = -(x / buttonBackground.width)
            overlayView.layoutParams.width = -(Math.min(x - buttonBackground.x - translation.toFloat(), -overlayView.width.toFloat()).toInt())
            DLog.e("hanmo x : $x")
            DLog.e("hanmo width : ${Math.min(x - overlayView.x - translation.toFloat(), buttonBackground.width.toFloat()).toInt()}")
            overlayView.requestLayout()

        } else {
            overlayView.alpha = 1f
            overlayView.layoutParams.width = buttonBackground.width
            overlayView.requestLayout()

        }
    }

    private fun calculateTranslation(x: Float): Int {
        return x.toInt() / -25
    }

    private fun cancelAnimations() {
        if (animator != null) {
            animator!!.cancel()
        }
        if (flingAnimation != null) {
            flingAnimation!!.cancel()
        }
    }

    private fun onDragFinished(finalX: Float) {
        DLog.e("hanmo onDragFinished : finalX $finalX  >  THRESHOLD : ${THRESHOLD_FRACTION * buttonBackground.width}")
        if (-finalX > THRESHOLD_FRACTION * buttonBackground.width) {
            animateToEnd(finalX)
        } else {
            animateToStart()
        }
    }

    private fun animateToStart() {
        cancelAnimations()
        animator = ValueAnimator.ofFloat(overlayView.width.toFloat(), 0f)
        animator!!.addUpdateListener { valueAnimator -> setDragProgress(valueAnimator.animatedValue as Float) }
        animator!!.duration = ANIMATE_TO_START_DURATION.toLong()
        animator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (triggered) {
                    completeSubject.onNext("start")
                }
            }
        })
        animator!!.start()
    }

    private fun animateToEnd(currentValue: Float) {
        cancelAnimations()
        var rightEdge = buttonBackground.width + buttonBackground.x
        rightEdge += calculateTranslation(rightEdge).toFloat()
        animator = ValueAnimator.ofFloat(currentValue, rightEdge)
        animator!!.addUpdateListener { valueAnimator -> setDragProgress(valueAnimator.animatedValue as Float) }
        animator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                triggered = true
                animateToStart()
            }
        })
        animator!!.duration = ANIMATE_TO_END_DURATION.toLong()
        animator!!.start()
    }

    private fun animateShakeButton() {
        cancelAnimations()
        var rightEdge = buttonBackground.width + buttonBackground.x
        rightEdge += calculateTranslation(rightEdge).toFloat()
        animator = ValueAnimator.ofFloat(0f, rightEdge, 0f, rightEdge / 2, 0f, rightEdge / 4, 0f)
        animator!!.interpolator = AccelerateDecelerateInterpolator()
        animator!!.addUpdateListener { valueAnimator ->
            val translation = calculateTranslation(valueAnimator.animatedValue as Float)
            setPadding(-translation, 0, +translation, 0)
        }
        animator!!.duration = ANIMATE_SHAKE_DURATION.toLong()
        animator!!.start()
    }

    companion object {

        val THRESHOLD_FRACTION = .85
        val ANIMATE_TO_START_DURATION = 300
        val ANIMATE_TO_END_DURATION = 200
        val ANIMATE_SHAKE_DURATION = 2000
        val FLING_FRICTION = .85f
    }
}
