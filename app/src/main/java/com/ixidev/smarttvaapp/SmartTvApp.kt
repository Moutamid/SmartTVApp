package com.ixidev.smarttvaapp

import androidx.multidex.MultiDexApplication
import com.fxn.stash.Stash
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by ABDELMAJID ID ALI on 31/10/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@HiltAndroidApp
class SmartTvApp : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        Stash.init(this)

    }
}