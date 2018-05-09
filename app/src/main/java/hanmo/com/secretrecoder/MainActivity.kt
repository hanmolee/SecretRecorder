package hanmo.com.secretrecoder

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import hanmo.com.secretrecoder.constants.RequestCodes
import hanmo.com.secretrecoder.constants.ResultCodes
import hanmo.com.secretrecoder.settings.SettingActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionMyApp()

        val settingIntent = SettingActivity.newIntent(applicationContext)
        //settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingIntent)

        /*val setting = SettingActivity()
        addContentFragment(R.id.contentFrame, setting)*/

    }

    fun Activity.addContentFragment(@IdRes frameId: Int, fragment: android.app.Fragment){
        fragmentManager.beginTransaction().add(frameId, fragment).commit()
    }

    private fun checkPermissionMyApp() {
        val RecordPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val OverlayPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
        val WritePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (RecordPermissionCheck == PackageManager.PERMISSION_DENIED
                && OverlayPermissionCheck == PackageManager.PERMISSION_DENIED
                && WritePermissionCheck == PackageManager.PERMISSION_DENIED) {

            startActivityForResult(Intent(this@MainActivity, OverlayPermissionActivity::class.java), RequestCodes.REQ_PERMISSION)
        } else {
            // 권한 있음
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)  {
            RequestCodes.REQ_PERMISSION -> {
                when(resultCode) {
                    ResultCodes.PERMISSION_SUCCESS -> {

                    }
                    ResultCodes.PERMISSION_FAIL -> {

                    }
                }
            }
        }

    }

}