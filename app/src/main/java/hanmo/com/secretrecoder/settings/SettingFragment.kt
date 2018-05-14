package hanmo.com.secretrecoder.settings

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.service.SettingButton
import hanmo.com.secretrecoder.service.StartRecordButton
import hanmo.com.secretrecoder.util.DLog
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * Created by hanmo on 2018. 5. 14..
 */
class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_settings, container, false)

        val userPreference = RealmHelper.instance.queryFirst(UserPreference::class.java)

        rootView?.let {
            setRecordSwitch(it, userPreference)
            setOverlaySwitch(it, userPreference)
            setTransparent(it, userPreference)
            setSettingSwitch(it, userPreference)
        }


        return rootView
    }

    private fun setTransparent(rootView: View, userPreference: UserPreference?) {

        userPreference?.let {
            with(rootView.transparentSeekbar) {

                progress = it.hasTransparent?.toInt()!!

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val intent = Intent("TRANSPARENT")
                        DLog.e("ddd $progress")
                        DLog.e("sss $seekBar")
                        intent.putExtra("tran", (progress / 100.0).toFloat())
                        activity.sendBroadcast(intent)

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

    private fun setOverlaySwitch(rootView: View, userPreference: UserPreference?) {

        userPreference?.let {
            with(rootView.lockscreenSwitch) {
                isChecked = it.hasOverlayLockscreen!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasOverlayLockscreen(isChecked)
                    activity.stopService(Intent(activity, StartRecordButton::class.java))
                    activity.startService(Intent(activity, StartRecordButton::class.java))
                    activity.stopService(Intent(activity, SettingButton::class.java))
                    activity.startService(Intent(activity, SettingButton::class.java))
                }
            }
        }
    }

    private fun setRecordSwitch(rootView: View, userPreference: UserPreference?) {

        userPreference?.let {
            with(rootView.recoderSwitch) {
                isChecked = it.hasRecord!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasRecord(isChecked)
                    if (isChecked) {
                        activity.startService(Intent(activity, StartRecordButton::class.java))
                    } else {
                        activity.stopService(Intent(activity, StartRecordButton::class.java))
                    }
                }
            }
        }
    }

    private fun setSettingSwitch(rootView: View, userPreference: UserPreference?) {
        userPreference?.let {
            with(rootView.settingSwitch) {
                isChecked = it.hasSetting!!
                setOnCheckedChangeListener { buttonView, isChecked ->
                    RealmHelper.instance.updatePreferenceHasSetting(isChecked)
                    if (isChecked) {
                        activity.startService(Intent(activity, SettingButton::class.java))
                    } else {
                        activity.stopService(Intent(activity, SettingButton::class.java))
                    }
                }
            }
        }
    }
    

}