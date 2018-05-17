package hanmo.com.secretrecoder.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.constants.Const
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.settings.SettingActivity
import hanmo.com.secretrecoder.util.DLog
import hanmo.com.secretrecoder.view.SwipeButton
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by hanmo on 2018. 5. 9..
 */
class SettingButton : Service() {

    private lateinit var wm: WindowManager
    private lateinit var mMenu: SwipeButton

    private val mFinished = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            context?.let {
                val actionName = intent.action
                when(actionName) {
                    Const.MENU_FINISH -> {
                        wm.removeView(mMenu)
                        setViewLayout()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        setReceiver()
        setViewLayout()
        setSwipeComplete()
    }

    private fun setReceiver() {
        val filter = IntentFilter()
        filter.addAction(Const.MENU_FINISH)
        registerReceiver(mFinished, filter)
    }


    private fun setViewLayout() {
        //val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

        mMenu = SwipeButton(applicationContext)


        val getUserPreference = RealmHelper.instance.queryFirst(UserPreference::class.java)
        getUserPreference?.let {
            if (it.hasOverlayLockscreen!!) {
                paramsHasLockscreen.gravity = Gravity.CENTER or Gravity.RIGHT
                wm.addView(mMenu, paramsHasLockscreen)
            } else {
                paramsHasNotLockscreen.gravity = Gravity.CENTER or Gravity.RIGHT
                wm.addView(mMenu, paramsHasNotLockscreen)
            }
        }
    }

    private fun setSwipeComplete() {
        mMenu.getProgressObservable().subscribe({ progress -> })
        mMenu.getCompleteObservable().subscribe({ string ->
            val settingIntent = SettingActivity.newIntent(applicationContext)
            settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(settingIntent)
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mFinished)
        wm.removeView(mMenu)
    }
}