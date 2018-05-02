package hanmo.com.secretrecoder.realm

import android.util.Log
import hanmo.com.secretrecoder.realm.model.UserPreference
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Created by hanmo on 2018. 5. 1..
 */
class RealmHelper {

    var realm: Realm
        private set

    init {
        realm = try {

            Realm.getDefaultInstance()

        } catch (e: Exception) {

            Log.d("Realm Exception", e.toString())

            val config = RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build()
            Realm.getInstance(config)
        }
    }

    fun initDB() {
        val userPreference = UserPreference()
        userPreference.id = 1
        userPreference.hasRecord = false

        addData(userPreference)
    }

    fun updatePreference(hasRecord : Boolean?) {
        val UserPreference = realm.where(UserPreference::class.java).findFirst()
        realm.executeTransaction {
            UserPreference?.hasRecord = hasRecord
        }
    }


    //Insert To Realm
    fun <T : RealmObject> addData(data: T) {
        realm.executeTransaction {
            realm.copyToRealm(data)
        }
    }

    //Insert To Realm with RealmList
    fun <T : RealmObject> addRealmListData(data: T) {
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(data)
        }
    }

    fun <T : RealmObject> queryAll(clazz: Class<T>): RealmResults<T>? {
        return realm.where(clazz).findAll()
    }

    fun <T : RealmObject> queryFirst(clazz: Class<T>): T? {
        return realm.where(clazz).findFirst()
    }

    companion object {

        private var INSTANCE: RealmHelper? = RealmHelper()

        val instance: RealmHelper
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RealmHelper()
                }
                return INSTANCE as RealmHelper
            }
    }

}