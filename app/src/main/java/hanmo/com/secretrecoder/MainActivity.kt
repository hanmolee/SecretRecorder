package hanmo.com.secretrecoder

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import hanmo.com.secretrecoder.service.StartRecordButton
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import hanmo.com.secretrecoder.constants.RequestCodes
import hanmo.com.secretrecoder.constants.ResultCodes


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissionMyApp()

        recoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startService(Intent(this@MainActivity, StartRecordButton::class.java))
            } else {
                stopService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        }
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