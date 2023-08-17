package com.ixidev.mobile.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.net.toUri
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ActivitySettingsBinding
import com.michaelflisar.gdprdialog.*
import com.michaelflisar.gdprdialog.helper.GDPRPreperationData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by ABDELMAJID ID ALI on 3/12/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(R.layout.activity_settings), GDPR.IGDPRCallback {

    private val binding: ActivitySettingsBinding by viewBinding()
    private var gdprSetup: GDPRSetup? = null

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        gdprSetup = provideGDPRSetup(this)
        setTitle(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.nightModeSwitch.isChecked = isDarkMode()
    }

    private fun showGdprDialog() {
        GDPR.getInstance().showDialog(this, gdprSetup, GDPRLocation.UNDEFINED)
    }

    private fun provideGDPRSetup(context: Context): GDPRSetup {
        return GDPRSetup(GDPRDefinitions.ADMOB, GDPRDefinitions.FIREBASE_CLOUD_MESSAGING)
            .withExplicitNonPersonalisedConfirmation(true)
            .withShowPaidOrFreeInfoText(false)
            .withForceSelection(true)
            .withBottomSheet(true)
            .withPrivacyPolicy(context.getString(com.ixidev.data.R.string.privacy_policy_url))
            .withCustomDialogTheme(com.ixidev.data.R.style.GDPR_Dialog)
            .withLoadAdMobNetworks(context.getString(com.ixidev.data.R.string.admob_publisher_id))
    }

    override fun onConsentNeedsToBeRequested(data: GDPRPreperationData?) {
        GDPR.getInstance().showDialog(this, gdprSetup, data?.location)
    }

    override fun onConsentInfoUpdate(consentState: GDPRConsentState?, isNewState: Boolean) {
        preferences.edit {
            putBoolean("use_personalized_ads", consentState?.consent?.isPersonalConsent == true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gdprSetup = null
    }

    fun switchDarkMode(@Suppress("UNUSED_PARAMETER") view: View) {
        onChangeTheme(binding.nightModeSwitch.isChecked)
    }

    fun openGdprDialog(@Suppress("UNUSED_PARAMETER") view: View) {
        showGdprDialog()
    }

    private fun onChangeTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun isDarkMode(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    fun openPrivacy(@Suppress("UNUSED_PARAMETER") view: View) {
        var url = getString(R.string.privacy_policy_url)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(browserIntent)
    }
    fun closeSub(@Suppress("UNUSED_PARAMETER") view: View) {
        var url = "https://play.google.com/store/account/subscriptions"
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(browserIntent)
    }


}