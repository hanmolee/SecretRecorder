package hanmo.com.secretrecoder

import android.support.multidex.MultiDexApplication
import hanmo.com.secretrecoder.realm.RealmHelper
import hanmo.com.secretrecoder.realm.model.UserPreference
import hanmo.com.secretrecoder.util.DLog
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by hanmo on 2018. 5. 1..
 */
class SecretRecordApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initDB()
    }

    private fun initDB() {
        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
                .name("secretrecord.realm")
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)


        val user = RealmHelper.instance.queryFirst(UserPreference::class.java)

        user?.let {
            DLog.e("기존 사용자")
        } ?: kotlin.run {
            RealmHelper.instance.initDB()
        }

    }


    override fun onTerminate() {
        super.onTerminate()
        if (!Realm.getDefaultInstance().isClosed) {
            Realm.getDefaultInstance().close()
        }
    }

}