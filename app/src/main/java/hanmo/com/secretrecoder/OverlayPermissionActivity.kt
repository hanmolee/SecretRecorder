package hanmo.com.secretrecoder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.secretrecoder.constants.RequestCodes
import hanmo.com.secretrecoder.constants.ResultCodes
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_permission.*

/**
 * Created by hanmo on 2018. 4. 26..
 */
class OverlayPermissionActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_permission)

        setButton()
    }

    private fun setButton() {
        permissionConfirm.clicks()
                .subscribe {
                    recordCheckPermissions()

                }
                .apply { compositeDisposable.add(this) }

        permissionDenied.clicks()
                .subscribe {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                .apply { compositeDisposable.add(this) }
    }

    fun recordCheckPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
                        RequestCodes.RecordRequestPermissionCode)
            } else {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestCodes.RecordRequestPermissionCode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Home", "Permission Granted")
                    startOverlayWindowService(this)
                } else {
                    Log.e("Home", "Permission Failed")

                }
            }
        }
    }

    //다시요청하는부분
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@OverlayPermissionActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), RequestCodes.RecordRequestPermissionCode)
    }

    fun startOverlayWindowService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            showObtainingPermissionOverlayWindow()

        } else {
            setResult(ResultCodes.PERMISSION_SUCCESS)
            finish()
        }
    }

    fun onOverlayResult(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                setResult(ResultCodes.PERMISSION_SUCCESS)
                finish()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun showObtainingPermissionOverlayWindow() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, RequestCodes.REQ_CODE_OVERLAY_PERMISSION)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.REQ_CODE_OVERLAY_PERMISSION -> onOverlayResult(this)

            else -> { }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {

    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
