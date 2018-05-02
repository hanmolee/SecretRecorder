package hanmo.com.secretrecoder.settings

import android.app.Fragment
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.service.StartRecordButton
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * Created by hanmo on 2018. 5. 1..
 */
class SettingFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        val userPreference = RealmHelper.instance.queryFirst(UserPreference::class.java)

        setRecordSwitch(rootView, userPreference)
        setOverlaySwitch(rootView, userPreference)
        setTransparent(rootView, userPreference)

        return rootView
    }

    private fun setTransparent(rootView: View, userPreference: UserPreference?) {

        userPreference?.let {
            with(rootView.transparentSeekbar) {

                progress = (it.hasTransparent!! * 100).toInt()

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

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
}