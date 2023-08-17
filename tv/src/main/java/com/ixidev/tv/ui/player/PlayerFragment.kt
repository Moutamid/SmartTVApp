package com.ixidev.tv.ui.player

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import com.ixidev.data.common.toast


/**
 * Created by ABDELMAJID ID ALI on 06/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
private const val TAG = "PlayerFragment"
class PlayerFragment : VideoSupportFragment() {

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = activity?.intent?.getStringExtra("videoUrl")
        val title = activity?.intent?.getStringExtra("videoTitle")
        val glueHost = VideoSupportFragmentGlueHost(this)
        val playerAdapter = MediaPlayerAdapter(activity)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
        mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        mTransportControlGlue.host = glueHost
        mTransportControlGlue.title = title
        mTransportControlGlue.playWhenPrepared()
        playerAdapter.setDataSource(Uri.parse(videoUrl))
    }

    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        try {
            super.onVideoSizeChanged(width, height)
        } catch (e: ArithmeticException) {
            Log.e(TAG, "onVideoSizeChanged: ", e)
        }
    }
    override fun onError(errorCode: Int, errorMessage: CharSequence?) {
        toast(errorMessage.toString())
    }
}