import java.util.*

/**
 * Created by ABDELMAJID ID ALI on 2/3/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
object AppConfig {
// com.gsesmartiptv.smartiptv
    const val APP_PACKAGE_NAME = "com.iptvsmarterspro.iptvsmarterslite"
    const val APP_VERSION_NAME = "0.0.5.1"
    const val APP_NAME = "Smart IPTV"

    // do not change this
    val APP_VERSION_CODE: Int
        get() {
            val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val year = Calendar.getInstance().get(Calendar.YEAR)
            return day * year
        }
}