package com.ixidev.data

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.gdprdialog.GDPR
import com.michaelflisar.gdprdialog.GDPRConsentState
import com.michaelflisar.gdprdialog.GDPRDefinitions
import com.michaelflisar.gdprdialog.GDPRSetup
import com.michaelflisar.gdprdialog.helper.GDPRPreperationData

/**
 * Created by ABDELMAJID ID ALI on 3/5/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
abstract class AbstractSplashActivity : AppCompatActivity(), GDPR.IGDPRCallback {

    private var gdprSetup: GDPRSetup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gdprSetup = provideGDPRSetup(this)
    }

    override fun onStart() {
        super.onStart()
        showGdprDialog()
    }

    abstract fun openMain()

    private fun showGdprDialog() {
        GDPR.getInstance().checkIfNeedsToBeShown(this, gdprSetup)
    }

    private fun provideGDPRSetup(context: Context): GDPRSetup {
        return GDPRSetup(GDPRDefinitions.ADMOB, GDPRDefinitions.FIREBASE_CLOUD_MESSAGING)
            .withExplicitNonPersonalisedConfirmation(true)
            .withShowPaidOrFreeInfoText(false)
            .withForceSelection(true)
            .withBottomSheet(true)
            .withPrivacyPolicy(context.getString(R.string.privacy_policy_url))
            .withCustomDialogTheme(R.style.GDPR_Dialog)
            .withLoadAdMobNetworks(context.getString(R.string.admob_publisher_id))
    }

    override fun onConsentNeedsToBeRequested(data: GDPRPreperationData?) {
        GDPR.getInstance().showDialog(this, gdprSetup, data?.location)
    }

    override fun onConsentInfoUpdate(consentState: GDPRConsentState?, isNewState: Boolean) {
        openMain()
    }

    override fun onDestroy() {
        super.onDestroy()
        gdprSetup = null
    }
}