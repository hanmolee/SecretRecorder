package hanmo.com.secretrecoder.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.service.SettingButton
import hanmo.com.secretrecoder.service.StartRecordButton
import hanmo.com.secretrecoder.util.DLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by hanmo on 2018. 5. 1..
 */
class SettingActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    companion object {

        fun newIntent(context: Context) : Intent {
            val intent = Intent(context, SettingActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.overridePendingTransition(R.anim.slide_in_right, 0)
        setContentView(R.layout.activity_settings)
        compositeDisposable = CompositeDisposable()

        val userPreference = RealmHelper.instance.queryFirst(UserPreference::class.java)

        setRecordSwitch(userPreference)
        setOverlaySwitch(userPreference)
        setTransparent(userPreference)
        setSettingSwitch(userPreference)
        setFinishSetting()

    }

    private fun setFinishSetting() {
        settingMenu.clicks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { finish() }
                .apply { compositeDisposable.add(this) }

    }

    private fun setTransparent(userPreference: UserPreference?) {

        userPreference?.let {
            with(transparentSeekbar) {

                progress = it.hasTransparent?.toInt()!!

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val intent = Intent("TRANSPARENT")
                        DLog.e("ddd $progress")
                        DLog.e("sss $seekBar")
                        intent.putExtra("tran", (progress / 100.0).toFloat())
                        sendBroadcast(intent)

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        RealmHelper.instance.updatePreferenceTransparent(seekBar?.progress)

                    }

                })
            }
        }
    }

    private fun setOverlaySwitch(userPreference: UserPreference?) {

        userPreference?.let {
            with(lockscreenSwitch) {
                isChecked = it.hasOverlayLockscreen!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasOverlayLockscreen(isChecked)
                    stopService(Intent(this@SettingActivity, StartRecordButton::class.java))
                    startService(Intent(this@SettingActivity, StartRecordButton::class.java))
                    stopService(Intent(this@SettingActivity, SettingButton::class.java))
                    startService(Intent(this@SettingActivity, SettingButton::class.java))
                }
            }
        }
    }

    private fun setRecordSwitch(userPreference: UserPreference?) {

        userPreference?.let {
            with(recoderSwitch) {
                isChecked = it.hasRecord!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasRecord(isChecked)
                    if (isChecked) {
                        startService(Intent(this@SettingActivity, StartRecordButton::class.java))
                    } else {
                        stopService(Intent(this@SettingActivity, StartRecordButton::class.java))
                    }
                }
            }
        }
    }

    private fun setSettingSwitch(userPreference: UserPreference?) {
        userPreference?.let {
            with(settingSwitch) {
                isChecked = it.hasSetting!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasSetting(isChecked)
                    if (isChecked) {
                        startService(Intent(this@SettingActivity, SettingButton::class.java))
                    } else {
                        stopService(Intent(this@SettingActivity, SettingButton::class.java))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        this.overridePendingTransition(0, R.anim.slide_out_right)
    }
}