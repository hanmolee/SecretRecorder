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
import android.content.pm.PackageManager
import android.Manifest.permission.RECORD_AUDIO
import android.support.v4.content.ContextCompat
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.support.v4.app.ActivityCompat

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

    fun recordCheckPermissions() : Boolean {
        val writeStorageResult = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val recordResult = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return writeStorageResult == PackageManager.PERMISSION_GRANTED && recordResult == PackageManager.PERMISSION_GRANTED
    }

    //다시요청하는부분
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO), RecordRequestPermissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            RecordRequestPermissionCode -> {
                if (grantResults.isNotEmpty()) {
                    val StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (StoragePermission and RecordPermission) {
                        //요청 수락
                        Toast.makeText(applicationContext, "accepted", Toast.LENGTH_SHORT).show()
                    } else {
                        //요청 거절
                        Toast.makeText(applicationContext, "denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun overalyCheckPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) { // 체크
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
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