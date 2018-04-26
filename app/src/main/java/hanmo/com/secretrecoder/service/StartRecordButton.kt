package hanmo.com.secretrecoder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import hanmo.com.secretrecoder.R
import android.widget.ImageButton
import android.widget.TextView
import android.view.Gravity
import android.view.WindowManager
import android.view.ViewGroup
import android.view.LayoutInflater
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_overlay.view.*
import java.util.concurrent.TimeUnit

/**
 * Created by hanmo on 2018. 4. 24..
 */
class StartRecordButton : Service() {

    private lateinit var wm: WindowManager
    private lateinit var mView: View
    private lateinit var compositeDisposable: CompositeDisposable
    private var recordStatus = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        compositeDisposable = CompositeDisposable()
        setRecordButton()
    }

    private fun setRecordButton() {
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

        params.gravity = Gravity.RIGHT or Gravity.TOP
        mView = inflate.inflate(R.layout.view_overlay, null)

        mView.recordButton.clicks()
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when(recordStatus) {
                        true -> {
                            //녹음중
                            recordStatus = false
                            mView.recordButton.setImageResource(R.drawable.ic_start_record)
                        }
                        false -> {
                            //녹음시작
                            recordStatus = true
                            mView.recordButton.setImageResource(R.drawable.ic_stop_record)
                        }
                    }
                }.apply { compositeDisposable.add(this) }

        wm.addView(mView, params)
    }

    private fun startRecording() {

    }

    private fun stopRecording() {

    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        wm.removeView(mView)
    }
}