package hanmo.com.secretrecoder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.KeyEvent.KEYCODE_BACK
import android.widget.FrameLayout
import android.R.attr.y
import android.R.attr.x
import android.R.attr.gravity
import android.content.Context
import android.graphics.PixelFormat
import android.content.Context.WINDOW_SERVICE
import android.view.*
import android.view.View.OnTouchListener
import hanmo.com.secretrecoder.R
import android.widget.ImageButton
import android.widget.TextView
import android.view.Gravity
import android.R.attr.gravity
import android.view.WindowManager
import android.view.ViewGroup
import android.view.LayoutInflater




/**
 * Created by hanmo on 2018. 4. 24..
 */
class StartRecordButton : Service() {

    lateinit var wm: WindowManager
    lateinit var mView: View

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        super.onCreate()
        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            /*ViewGroup.LayoutParams.MATCH_PARENT*/300,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.LEFT or Gravity.TOP
        mView = inflate.inflate(R.layout.view_overlay, null)
        val textView = mView.findViewById(R.id.textView) as TextView
        val bt = mView.findViewById(R.id.bt) as ImageButton
        bt.setOnClickListener {
            textView.text = "on click!!"
        }
        wm.addView(mView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
            }
        }
    }
}