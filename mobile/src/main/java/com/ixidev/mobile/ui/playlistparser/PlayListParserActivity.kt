package com.ixidev.mobile.ui.playlistparser

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.adstoolkit.core.FullScreenAdsListener
import com.ixidev.adstoolkit.core.IInterstitialAd
import com.ixidev.data.FileParserViewModel
import com.ixidev.data.common.AppTask
import com.ixidev.data.common.EncryptionUtils.decrypt
import com.ixidev.data.common.extracts
import com.ixidev.data.common.hasNetworkConnection
import com.ixidev.data.common.toast
import com.ixidev.data.logger.*
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ActivityPlayListParserBinding
import com.ixidev.mobile.ui.common.SaveSharedPreference
import com.ixidev.smarttvapp.ads.AddFileInterstitialAdId
import com.yodo1.mas.Yodo1Mas
import com.yodo1.mas.error.Yodo1MasError
import com.yodo1.mas.event.Yodo1MasAdEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("unused")
private const val TAG = "PlayListParserActivity"

@AndroidEntryPoint
class PlayListParserActivity : AppCompatActivity(R.layout.activity_play_list_parser),
    FullScreenAdsListener {

    private val viewModel: FileParserViewModel by viewModels()
    private val binding: ActivityPlayListParserBinding by viewBinding()
    private val playList: MoviesPlayList? by extracts()

    @Inject
    lateinit var interstitialAd: IInterstitialAd

    @Inject
    lateinit var logger: IAppLogger

    @Inject
    @AddFileInterstitialAdId
    lateinit var intersAdId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.parsFileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        onParseFile()
        // Yodo1 interstitialAd.load(this, intersAdId.decrypt())

        if (SaveSharedPreference.showAds(this)) {
            initIV() // Yodo1
        }
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
                finish()
            }
        }
        Yodo1Mas.getInstance().setInterstitialListener(interstitialListener)
    }

    private  fun showIV() {
        print("Yodo1 MAS : Show IV")
        if (SaveSharedPreference.showAds(this)) {
            if (Yodo1Mas.getInstance().isInterstitialAdLoaded())
                Yodo1Mas.getInstance().showInterstitialAd(this);
        }

        finish()

    }
    // end Yodo1


    private fun onParseFile() {
        playList?.let {
            viewModel.parsFilesTask.observe(this) { task ->
                task?.let {
                    onParseFileTaskChange(task)
                }
            }
            viewModel.parsePlayList(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onParseFileTaskChange(task: AppTask<*>) {
        when (task) {
            is AppTask.Loading -> {
                binding.progressBar.isVisible = true
                binding.progressMessage.text = task.message
            }
            is AppTask.Progressing -> {
                binding.progressPercentage.text = "${task.progress}%"
                binding.progressPercentage.isVisible = true
            }
            is AppTask.Success -> {
                onParseFileSuccess(task.data)
            }
            is AppTask.Error -> {
                onError(task.error)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onError(error: Exception) {
        binding.progressPercentage.isVisible = false
        binding.progressBar.isVisible = false
        Log.e("TESTING123", error.stackTraceToString());
        if (hasNetworkConnection())
            binding.progressMessage.text = " Error : ${error.localizedMessage}"
        else
            binding.progressMessage.text = getString(R.string.network_error_message)
        binding.buttonRetry.apply {
            isVisible = true
            setOnClickListener {
                onParseFile()
            }
        }
    }

    private fun onParseFileSuccess(message: Any?) {
        logger.logEvent(EVENT_PARSE_FILE_COMPLETE)
        toast("Parsing file successfully ($message) channels")
        onBackPressed()
    }

    override fun onNavigateUp(): Boolean {
        return onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        // Yodo1 interstitialAd.show(this, this)

        showIV() // Yodo1
    }

    override fun onStop() {
        super.onStop()
        this.interstitialAd.destroy()
        this.interstitialAd.listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        this.interstitialAd.destroy()
    }

    override fun onAdDismissed(isShowed: Boolean) {
        if (isShowed)
            logger.logEvent(
                EVENT_CLOSE_FULL_SCREEN_AD,
                bundleOf(
                    EVENT_EXTRACTS_SHOW_IN to "ParseFileActivity"
                )
            )
        finish()
    }

    override fun onAdShowed() {
        logger.logEvent(
            EVENT_SHOW_FULL_SCREEN_AD,
            bundleOf(
                EVENT_EXTRACTS_SHOW_IN to "ParseFileActivity"
            )
        )
    }

    override fun onShowAdFailed(error: Exception) {
        Log.e(TAG, "onShowAdFailed: ", error)
    }
}