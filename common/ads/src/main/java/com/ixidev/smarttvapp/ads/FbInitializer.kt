package com.ixidev.smarttvapp.ads

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Created by ABDELMAJID ID ALI on 3/13/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class FbInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        if (BuildConfig.DEBUG) {
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(BuildConfig.ADS_TEST_DEVICE_ID))
            MobileAds.setRequestConfiguration(
                requestConfiguration.build()
            )
        }
        MobileAds.initialize(context!!) {

        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return -1
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return -1
    }
}