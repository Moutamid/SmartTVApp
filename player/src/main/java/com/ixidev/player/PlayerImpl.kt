package com.ixidev.player

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.cast.framework.CastContext
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.cast.MediaInfo
import org.jetbrains.anko.runOnUiThread

/**
 * Created by ABDELMAJID ID ALI on 2/16/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class PlayerImpl(
    private val context: Context,
    private val playerState: PlayerState,
    private val playerView: StyledPlayerView,
    private val videoListener: Player.Listener,
    private val castContext: CastContext
) {


    val isCurrentWindowLive: Boolean
        get() = localPlayer.isCurrentWindowLive
    private var _localPlayer: ExoPlayer? = null
    val localPlayer: ExoPlayer
        get() = _localPlayer!!

    private var _castPlayer: CastPlayer? = null
    val castPlayer: CastPlayer
        get() = _castPlayer!!

    private var currentPlayer: Player? = null

    private var _trackSelector: DefaultTrackSelector? = null
    val trackSelector: DefaultTrackSelector
        get() = _trackSelector!!

    private var url = ""


    init {
        _trackSelector = DefaultTrackSelector(context)
        _localPlayer = createPlayer(context).also {
            it.addListener(videoListener)
            playerView.player = it
        }

        //Cast Player Initialization
        _castPlayer = CastPlayer(castContext)
        _castPlayer?.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                setCurrentPlayer(castPlayer)
            }

            override fun onCastSessionUnavailable() {
                setCurrentPlayer(localPlayer)
            }

        })

        val isCastSessionAvailable = _castPlayer?.isCastSessionAvailable ?: false

        setCurrentPlayer(if (isCastSessionAvailable) castPlayer else localPlayer)

    }

    private fun createPlayer(context: Context): SimpleExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context)
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        return SimpleExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(DefaultLoadControl())
            .build()
    }

    private fun setCurrentPlayer(currentPlayer: Player) {
        if (this.currentPlayer == currentPlayer)
            return
        playerView.player = currentPlayer
        playerView.controllerHideOnTouch = currentPlayer == _localPlayer

        if (currentPlayer == castPlayer) {
            playerView.controllerShowTimeoutMs = 0;
            playerView.showController();
            playerView.defaultArtwork = ResourcesCompat.getDrawable(
                playerView.context.resources,
                R.drawable.ic_baseline_cast_connected_400,
                /* theme= */ null
            )
        } else { // currentPlayer == localPlayer
            playerView.controllerShowTimeoutMs = StyledPlayerControlView.DEFAULT_SHOW_TIMEOUT_MS;
            playerView.defaultArtwork = null;
        }

        // Player state management.
        // Player state management.
        var playbackPositionMs = C.TIME_UNSET
        var currentItemIndex = C.INDEX_UNSET
        var playWhenReady = false

        val previousPlayer = this.currentPlayer
        if (previousPlayer != null) {
            // Save state from the previous player.
            val playbackState = previousPlayer.playbackState
            if (playbackState != Player.STATE_ENDED) {
                playbackPositionMs = previousPlayer.currentPosition
                playWhenReady = previousPlayer.playWhenReady
                currentItemIndex = previousPlayer.currentMediaItemIndex
                if (currentItemIndex != currentItemIndex) {
                    playbackPositionMs = C.TIME_UNSET
                    currentItemIndex = currentItemIndex
                }
            }
            previousPlayer.stop()
            previousPlayer.clearMediaItems()
        }

        this.currentPlayer = currentPlayer
        start(url)
    }


    // Prepare playback.
    fun start(url: String) {
        Log.d("++url++", url)
        if (url.isNotEmpty()) {
            this.url = url
            if (currentPlayer == localPlayer) {

                localPlayer.setMediaItem(MediaItem.fromUri(url))
                localPlayer.prepare()
                with(playerState) {
                    localPlayer.playWhenReady = whenReady
                    localPlayer.seekTo(window, position)
                }

            }
            if (currentPlayer == castPlayer) {
                HlsCastUrlExtractor.extractUrl(url.replace(".ts",".m3u8")) { castUrl ->
                    context.runOnUiThread {
                        castUrl?.let { nonNullUrl ->
                            castContext.sessionManager.currentCastSession?.remoteMediaClient?.let { mediaClient ->
                                val mediaInfo = MediaInfo.Builder(nonNullUrl)
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setContentType(MimeTypes.BASE_TYPE_VIDEO)
                                    .build()


                                mediaClient.load(mediaInfo, true, 0)
                            }

                        }

                }


            }


            /*  val mediaItem = MediaItem.Builder()
                  .setUri(url)
                  .setMediaMetadata(
                      MediaMetadata
                          .Builder()
                          .setTitle("Cast")
                          .build()
                  )
                  .setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url)))
                  .build()
              castPlayer.setMediaItem(mediaItem)
              castPlayer.play()
              with(playerState) {
                  castPlayer.playWhenReady = whenReady
                  //castPlayer.seekTo(window, position)
              }*/


        }
    }
}

private fun buildMediaQueueItem(mediaSourceInfo: MediaSourceInfo, position: Int): MediaItem? {
    val movieMetadata = MediaMetadata.Builder()
    movieMetadata.setTitle(mediaSourceInfo.title)
    movieMetadata.setArtist(mediaSourceInfo.author)
    movieMetadata.setArtworkUri(Uri.parse(mediaSourceInfo.imageUrl))
    movieMetadata.setTrackNumber(position)
    val builder = MediaItem.Builder()
    builder.setMimeType(if (mediaSourceInfo.isVideo) MimeTypes.VIDEO_UNKNOWN else MimeTypes.AUDIO_UNKNOWN)
    if (mediaSourceInfo.url
            .contains(".m3u8")
    ) builder.setMimeType(MimeTypes.APPLICATION_M3U8)
    builder.setUri(mediaSourceInfo.url)
    builder.setMediaMetadata(movieMetadata.build())
    return builder.build()
}

fun stop() {
    currentPlayer?.let {
        with(it) {
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }
            stop()
        }
    }
}

// Destroy the player instance.
fun release() {
    localPlayer.release()
    castPlayer.setSessionAvailabilityListener(null)
    castPlayer.release()
    _localPlayer = null
    _castPlayer = null
    _trackSelector = null
}
}

data class PlayerState(
    var window: Int = 0,
    var position: Long = 0,
    var whenReady: Boolean = true
)