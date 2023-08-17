package com.ixidev.mobile.ui.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SaveSharedPreference {

    companion object{

        val PREF_ADS = "ads_subs"

        private fun getSharedPreferences(ctx: Context): SharedPreferences {
            return  ctx.getSharedPreferences("ads_subs", MODE_PRIVATE)
        }

        fun setAdsPref(ctx: Context, flag: Boolean) {
            val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
            editor.putBoolean(PREF_ADS, flag)
            editor.apply()
        }

        fun showAds(ctx: Context): Boolean {
            return getSharedPreferences(ctx)
                .getBoolean(PREF_ADS, true)
        }
    }



}