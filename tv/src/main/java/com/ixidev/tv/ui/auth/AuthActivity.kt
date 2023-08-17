package com.ixidev.tv.ui.auth

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.ixidev.tv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : FragmentActivity(R.layout.activity_auth) {


    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AuthActivity::class.java)
            context.startActivity(starter)
        }
    }
}