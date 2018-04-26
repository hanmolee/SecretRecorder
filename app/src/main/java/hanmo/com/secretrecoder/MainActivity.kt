package hanmo.com.secretrecoder

import android.Manifest
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
import android.content.pm.PackageManager
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.support.v4.app.ActivityCompat
import android.util.Log


class MainActivity : AppCompatActivity() {

    private val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1
    private val RecordRequestPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                overalyCheckPermission()
                recordCheckPermissions()
            } else {
                stopService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        }
    }

    fun recordCheckPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
                        RecordRequestPermissionCode)
            } else {

            }
        }
    }

    //다시요청하는부분
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO), RecordRequestPermissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RecordRequestPermissionCode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Home", "Permission Granted")

                } else {
                    Log.e("Home", "Permission Failed")

                }
            }
        }
    }


    private fun overalyCheckPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
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