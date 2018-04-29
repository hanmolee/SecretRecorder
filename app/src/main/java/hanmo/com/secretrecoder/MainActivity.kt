package hanmo.com.secretrecoder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import hanmo.com.secretrecoder.service.StartRecordButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityForResult(Intent(this@MainActivity, OverlayPermissionActivity::class.java), 2123)

        recoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startService(Intent(this@MainActivity, StartRecordButton::class.java))
            } else {
                stopService(Intent(this@MainActivity, StartRecordButton::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}