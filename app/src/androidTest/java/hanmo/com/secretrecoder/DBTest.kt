package hanmo.com.secretrecoder

import android.support.test.InstrumentationRegistry
import hanmo.com.secretrecoder.realm.model.UserPreference
import io.realm.Realm
import io.realm.RealmConfiguration
import junit.framework.Assert
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

/**
 * Created by hanmo on 2018. 5. 1..
 */
@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DBTest {

    private lateinit var realm: Realm

    @Before
    fun initDB() {
        Realm.init(InstrumentationRegistry.getTargetContext())

        val realmConfiguration = RealmConfiguration.Builder()
                .name("test.realm")
                .inMemory()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)

        realm = Realm.getDefaultInstance()
    }

    @Test
    fun A_initUserPreference() {
        val userPreference = UserPreference()
        userPreference.id = 1
        userPreference.hasRecord = false

        realm.executeTransaction {
            realm.copyToRealm(userPreference)
        }

        val testUserPreference = realm.where(UserPreference::class.java).findFirst()
        assertNotNull(testUserPreference)
        testUserPreference?.let {
            assertEquals(1, it.id)
            assertEquals(false, it.hasRecord)
        }

    }

    @Test
    fun B_updatePreference() {
        val userPreference = realm.where(UserPreference::class.java).findFirst()
        assertNotNull(userPreference)
        userPreference?.let {
            assertEquals(1, it.id)
            assertEquals(false, it.hasRecord)
        }
        realm.executeTransaction {
            userPreference?.hasRecord = true
        }

        val testUserPreference = realm.where(UserPreference::class.java).findFirst()
        assertNotNull(testUserPreference)
        testUserPreference?.let {
            assertEquals(1, it.id)
            assertEquals(true, it.hasRecord)
        }
    }

    @After
    fun closeDB() {
        if (!realm.isClosed) {
            realm.close()
        }
    }
}