package hanmo.com.secretrecoder.util

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by hanmo on 2018. 4. 29..
 */
class GestureListener : GestureDetector.SimpleOnGestureListener() {


    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return super.onDoubleTap(e)
    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        return super.onContextClick(e)
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return super.onDoubleTapEvent(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        super.onLongPress(e)
    }
}