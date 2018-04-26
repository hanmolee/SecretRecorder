package hanmo.com.secretrecoder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.provider.Settings
import android.content.Intent
import android.net.Uri
import hanmo.com.secretrecoder.service.StartRecordButton
import android.annotation.TargetApi
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkPermission()
            } else {
                stopService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            } else {
                startService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        } else {
            startService(Intent(this@MainActivity, StartRecordButton::class.java))
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
                Toast.makeText(applicationContext, "동의 안함", Toast.LENGTH_SHORT).show()

            } else {
                startService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        }
    }
}