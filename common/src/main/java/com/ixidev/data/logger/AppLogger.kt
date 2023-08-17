package com.ixidev.data.logger

import android.os.Bundle
import android.util.Log

interface IAppLogger {
    val tag: String
    val debug: Boolean
    fun logEvent(event: String, extracts: Bundle? = null)
}

/**
 * Created by ABDELMAJID ID ALI on 3/9/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class AppLogger(
    override val tag: String,
    override val debug: Boolean
) : IAppLogger {
    override fun logEvent(event: String, extracts: Bundle?) {
        if (debug) {
            Log.d(tag, "Event: $event , extracts = $extracts")
        }
    }

}