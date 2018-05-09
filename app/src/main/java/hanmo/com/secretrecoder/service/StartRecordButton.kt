package hanmo.com.secretrecoder.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.view.*
import hanmo.com.secretrecoder.R
import android.view.Gravity
import android.view.WindowManager
import android.view.ViewGroup
import android.view.LayoutInflater
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_overlay.view.*
import java.util.concurrent.TimeUnit
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.util.*
import android.widget.Toast
import java.io.IOException
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.OnTouchListener
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.settings.SettingActivity
import hanmo.com.secretrecoder.util.DLog
import hanmo.com.secretrecoder.view.SwipeButton
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast


/**
 * Created by hanmo on 2018. 4. 24..
 */
class StartRecordButton : Service() {

    private lateinit var wm: WindowManager
    private lateinit var mView: View
    private lateinit var mMenu: SwipeButton
    private lateinit var compositeDisposable: CompositeDisposable
    private var recordStatus = false

    private lateinit var AudioSavePathInDevice: String
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var random: Random
    private var AudioFileName = "HANMO"

    private val mTransparentReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            context?.let {
                val actionName = intent.action
                DLog.e("ACION NAME : $actionName")
                when(actionName) {
                    "TRANSPARENT" -> {
                        val ss = intent.getFloatExtra("tran", 0f)
                        DLog.e(ss.toString())
                        mView.alpha = ss
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        compositeDisposable = CompositeDisposable()
        setReceiver()
        setViewLayout()
        setRecordButton()

    }

    private fun setReceiver() {
        val filter = IntentFilter()
        filter.addAction("TRANSPARENT")
        registerReceiver(mTransparentReceiver, filter)
    }

    private fun setRecordReady() {
        random = Random()
        AudioSavePathInDevice = Environment.getExternalStorageDirectory().absolutePath + "/" +
                CreateRandomAudioFileName() + "AudioRecording.3gp"
    }

    private fun setViewLayout() {
        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

         val paramsHasLockscreen = WindowManager.LayoutParams(
                 WindowManager.LayoutParams.WRAP_CONTENT,
                 WindowManager.LayoutParams.WRAP_CONTENT,
                         WindowManager.LayoutParams.TYPE_TOAST,
                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                         or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                         or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                         or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                 PixelFormat.TRANSLUCENT)

        val paramsHasNotLockscreen = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT)

        mView = inflate.inflate(R.layout.view_overlay, null)
        mMenu = SwipeButton(applicationContext)


        val getUserPreference = RealmHelper.instance.queryFirst(UserPreference::class.java)
        getUserPreference?.let {
            if (it.hasOverlayLockscreen!!) {
                paramsHasLockscreen.gravity = Gravity.RIGHT or Gravity.TOP
                wm.addView(mView, paramsHasLockscreen)
                paramsHasLockscreen.gravity = Gravity.CENTER or Gravity.RIGHT
                wm.addView(mMenu, paramsHasLockscreen)
            } else {
                paramsHasNotLockscreen.gravity = Gravity.RIGHT or Gravity.TOP
                wm.addView(mView, paramsHasNotLockscreen)
                paramsHasNotLockscreen.gravity = Gravity.CENTER or Gravity.RIGHT
                wm.addView(mMenu, paramsHasNotLockscreen)
            }
        }
    }

    private fun setRecordButton() {
        /*mView.setOnTouchListener(object : GestureDetector.SimpleOnGestureListener(), OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Toast.makeText(applicationContext, "터치함", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                Toast.makeText(applicationContext, "더블탭 !!", Toast.LENGTH_SHORT).show()
                return super.onDoubleTap(e)
            }

        })*/


        mMenu.getProgressObservable().subscribe({ progress -> })
        mMenu.getCompleteObservable().subscribe({ string ->
            val settingIntent = SettingActivity.newIntent(applicationContext)
            settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(settingIntent)
        })

        //더블탭 구현해야 함
        mView.recordButton.clicks()
                .debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when(recordStatus) {
                        true -> {
                            //녹음중
                            recordStatus = false
                            mView.recordButton.setImageResource(R.drawable.ic_start_record)
                            stopRecording()

                        }
                        false -> {
                            //녹음시작
                            recordStatus = true
                            mView.recordButton.setImageResource(R.drawable.ic_stop_record)
                            startRecording()
                        }
                    }
                }.apply { compositeDisposable.add(this) }

    }

    private fun MediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setOutputFile(AudioSavePathInDevice)


    }

    private fun CreateRandomAudioFileName(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(AudioFileName).append(System.currentTimeMillis())

        return stringBuilder.toString()
    }

    private fun startRecording() {
        setRecordReady()
        MediaRecorderReady()
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IllegalStateException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        //Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show()
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        //Toast.makeText(this, "Recording Completed", Toast.LENGTH_LONG).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        unregisterReceiver(mTransparentReceiver)
        wm.removeView(mView)
        wm.removeView(mMenu)
    }

}