package com.ixidev.mobile.ui.player

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.core.os.bundleOf
import com.ixidev.adstoolkit.core.FullScreenAdsListener
import com.ixidev.adstoolkit.core.IInterstitialAd
import com.ixidev.data.common.EncryptionUtils.decrypt
import com.ixidev.data.logger.*
import com.ixidev.mobile.ui.common.SaveSharedPreference
import com.ixidev.player.ExoPlayerActivity
import com.ixidev.smarttvapp.ads.BuildConfig
import com.ixidev.smarttvapp.ads.PlayerInterstitialAdId
import com.yodo1.mas.Yodo1Mas
import com.yodo1.mas.error.Yodo1MasError
import com.yodo1.mas.event.Yodo1MasAdEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 2/6/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@AndroidEntryPoint
class MobilePlayerActivity : ExoPlayerActivity(), FullScreenAdsListener {


    private var currentUrl: String? = null

    private val viewModel: PlayerViewModel by viewModels()

    @Inject
    lateinit var interstitialAd: IInterstitialAd

    @Inject
    lateinit var logger: IAppLogger

    @Inject
    @PlayerInterstitialAdId
    lateinit var playerIntersId: String

    @Inject
    lateinit var preferences: SharedPreferences

    private val adapter: PlayerChannelsAdapter by lazy {
        PlayerChannelsAdapter(
            movieClickListener = {
                currentUrl = it.sourceUrl!!
                play()
                title = it.title
            },
            movieFavChange = {
                viewModel.changeChannelItemFav(it)
            },
        )
    }


    private fun play() {
        adapter.setSelectedUrl(currentUrl!!)
        startPlayer(currentUrl!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMenuItemsAdapter(adapter)
        initObservers()
        initAds()
        handleIntent(intent)

    }

    private fun initAds() {
        if (SaveSharedPreference.showAds(this)) {
            initIV() // Yodo1
        }
        /* Yodo1
        if (preferences.canLoadIntersAds())
            interstitialAd.load(this, playerIntersId.decrypt()) */
    }

    // Yodo1
    private fun initIV() {
        print("Yodo1 MAS : Init IV")
        val interstitialListener: Yodo1Mas.InterstitialListener = object : Yodo1Mas.InterstitialListener() {
            override fun onAdOpened(event: Yodo1MasAdEvent) {

            }
            override fun onAdError(event: Yodo1MasAdEvent, error: Yodo1MasError) {
                // Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
            override fun onAdClosed(event: Yodo1MasAdEvent) {

            }
        }
        Yodo1Mas.getInstance().setInterstitialListener(interstitialListener)
    }

    private  fun showIV() {
        print("Yodo1 MAS : Show IV")

        if (SaveSharedPreference.showAds(this)) {
            if (Yodo1Mas.getInstance().isInterstitialAdLoaded() && preferences.canLoadIntersAds()) {
                print("Yodo1 MAS : IV showed")
                Yodo1Mas.getInstance().showInterstitialAd(this);
            }
        }

        preferences.incrementAdCount()
        finish()

    }
    // end Yodo1

    private fun handleIntent(newIntent: Intent) {
        newIntent.extras?.let { bundle ->
            bundle.getString("url")?.let { url ->
                currentUrl = url
            }
            bundle.getString("title")?.let { title ->
                setTitle(title)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
            play()
        }
    }

    private fun initObservers() {
        viewModel.channels.observe(this) { channels ->
            adapter.submitData(lifecycle, channels)
        }
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.onSearch(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.onSearch(query)
        return true
    }

    override fun onStart() {
        super.onStart()
        play()
    }

    override fun onBackPressed() {
        if (isMenuOpen()) {
            closePlayerMenu()
            return
        }
        stopPlayer()
        // Yodo1 interstitialAd.show(this, this)
        showIV() // Yodo1
    }

    override fun onAdDismissed(isShowed: Boolean) {
        if (isShowed) {
            logger.logEvent(
                EVENT_SHOW_FULL_SCREEN_AD,
                bundleOf(
                    EVENT_EXTRACTS_SHOW_IN to "PlayerActivity"
                )
            )
        }
        preferences.incrementAdCount()
        finish()
    }

    override fun onAdShowed() {
        logger.logEvent(
            EVENT_CLOSE_FULL_SCREEN_AD,
            bundleOf(
                EVENT_EXTRACTS_SHOW_IN to "PlayerActivity"
            )
        )
    }

    override fun onShowAdFailed(error: Exception) {
        Log.e("PlayerActivity", "onShowAdFailed: ", error)
    }

    override fun onStop() {
        super.onStop()
        this.interstitialAd.listener = null
        this.interstitialAd.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAd.destroy()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode)
            logger.logEvent(EVENT_PIC_IN_PIC)
    }

    companion object {
        @JvmStatic
        fun start(
            context: Context,
            url: String,
            title: String? = null,
            category: String? = null,
            listId: Int? = -1
        ) {
            val starter = Intent(context, MobilePlayerActivity::class.java)
                .putExtra("url", url)
                .putExtra("title", title)
                .putExtra("category", category)
                .putExtra("listId", listId)
            context.startActivity(starter)
        }
    }
}

private fun SharedPreferences.incrementAdCount() {
    edit {
        val count = getInt("inters_count", 0)
        putInt("inters_count", count + 1)
    }
}

private fun SharedPreferences.canLoadIntersAds(): Boolean {
    val count = getInt("inters_count", 0)
    return (count == 0) or (count % BuildConfig.BACK_PRESS_CLICKS == 0)
}