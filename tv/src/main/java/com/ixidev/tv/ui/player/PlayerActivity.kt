package com.ixidev.tv.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Created by ABDELMAJID ID ALI on 06/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

class PlayerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, PlayerFragment())
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, videoTitle: String?, videoUrl: String?) {
            val starter = Intent(context, PlayerActivity::class.java)
                .putExtra("videoTitle", videoTitle)
                .putExtra("videoUrl", videoUrl)
            context.startActivity(starter)
        }
    }
}