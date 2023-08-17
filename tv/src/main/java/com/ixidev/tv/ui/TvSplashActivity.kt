package com.ixidev.tv.ui

import androidx.core.os.bundleOf
import com.ixidev.data.AbstractSplashActivity
import com.ixidev.data.logger.EVENT_EXTRACTS_DEVICE
import com.ixidev.data.logger.EVENT_OPEN_APP
import com.ixidev.data.logger.IAppLogger
import com.ixidev.tv.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 3/5/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@AndroidEntryPoint
class TvSplashActivity : AbstractSplashActivity() {

    @Inject
    lateinit var logger: IAppLogger

    override fun openMain() {
        logger.logEvent(
            EVENT_OPEN_APP,
            bundleOf(
                EVENT_EXTRACTS_DEVICE to "tv"
            )
        )
        AuthActivity.start(this)
        finish()
    }
}