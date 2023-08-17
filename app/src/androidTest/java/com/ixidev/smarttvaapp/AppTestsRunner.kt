package com.ixidev.smarttvaapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Created by ABDELMAJID ID ALI on 3/13/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */


class AppTestsRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

}