package hanmo.com.secretrecoder.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by hanmo on 2018. 5. 1..
 */
open class UserPreference : RealmObject() {

    @PrimaryKey
    open var id : Int = 0

    open var hasRecord : Boolean? = null

    open var hasOverlayLockscreen : Boolean? = null

    open var hasTransparent : Float? = null
}