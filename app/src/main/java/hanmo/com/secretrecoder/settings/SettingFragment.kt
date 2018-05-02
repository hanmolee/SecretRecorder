package hanmo.com.secretrecoder.settings

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.secretrecoder.R
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.service.StartRecordButton
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by hanmo on 2018. 5. 1..
 */
class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_settings, container, false)

        setRecordButton()

        return rootView
    }

    private fun setRecordButton() {
        val prefer = RealmHelper.instance.queryFirst(UserPreference::class.java)

        prefer?.let {
            recoderSwitch.isChecked = it.hasRecord!!
        }

        recoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                RealmHelper.instance.updatePreference(isChecked)
                activity.startService(Intent(activity, StartRecordButton::class.java))
            } else {
                RealmHelper.instance.updatePreference(isChecked)
                activity.stopService(Intent(activity, StartRecordButton::class.java))
            }
        }
    }
}