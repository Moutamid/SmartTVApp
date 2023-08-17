package com.ixidev.player

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.ixidev.player.databinding.ActivityAbstractExoPlayerBinding
import org.jetbrains.anko.toast


open class ExoPlayerActivity : AppCompatActivity(R.layout.activity_abstract_exo_player),
    Player.Listener, SearchView.OnQueryTextListener {

    private var currentResizeMode = 0

    private val view: ActivityAbstractExoPlayerBinding by viewBinding()
    private var player: PlayerImpl? = null
    private val playerState by lazy { PlayerState() }
    private var castContext: CastContext? = null
    private var trackInfoItem: MenuItem? = null
    private val btnSizeMode: AppCompatImageView by lazy {
        view.root.findViewById<AppCompatImageView>(R.id.btn_screen).apply {
            setOnClickListener {
                toggleResizeMode(currentResizeMode + 1)
            }
        }
    }

    // Android lifecycle hooks.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(view.playerToolbar)
        try {
            castContext = CastContext.getSharedInstance(this)
        } catch (e: RuntimeException) {
            Log.d("++cast++", "${e.message}")
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        volumeControlStream = AudioManager.STREAM_MUSIC
        if (savedInstanceState != null) {
            playerState.whenReady = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            playerState.window = savedInstanceState.getInt(KEY_WINDOW)
            playerState.position = savedInstanceState.getLong(KEY_POSITION)
            currentResizeMode = savedInstanceState.getInt(KEY_RESIZE_MODE)
        } else {
            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        createPlayer()
    }

    private fun toggleMenuOpenClose() {
        if (isMenuOpen()) {
            closePlayerMenu()
        } else {
            openPlayerMenu()
        }
    }

    private fun openPlayerMenu() {
        view.root.openDrawer(view.playerNavigation)
    }

    fun isMenuOpen() = view.root.isDrawerOpen(view.playerNavigation)

    fun closePlayerMenu() {
        view.root.closeDrawer(GravityCompat.END)
    }

    fun setMenuItemsAdapter(adapter: RecyclerView.Adapter<*>) {
        view.playerChannelsRecyclerView.adapter = adapter
    }


    @Deprecated("Deprecated in Java")
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        view.videoLoadingProgress.isVisible = (playbackState == Player.STATE_BUFFERING)
        view.playerErrorText.isVisible =
            (playbackState != Player.STATE_READY) && (playbackState != Player.STATE_BUFFERING)
        toggleLiveState(player!!.isCurrentWindowLive)
        toggleTrackInfoBtn()
    }

    private fun toggleTrackInfoBtn() {
        trackInfoItem?.isEnabled = TrackSelectionDialog.willHaveContent(player!!.trackSelector)
    }

    private fun toggleLiveState(isLive: Boolean) {
        view.root.findViewById<View>(R.id.player_live_state).isVisible = isLive
    }

    private fun toggleResizeMode(resizeMode: Int) {
        this.currentResizeMode = resizeMode
        if (resizeMode >= sizeModes.size) {
            this.currentResizeMode = 0
        }
        view.playerView.resizeMode = this.currentResizeMode
        try {
            this.btnSizeMode.setImageResource(sizeModes[this.currentResizeMode]!!)
        } catch (e: Exception) {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.player_menu, menu)
        trackInfoItem = menu.findItem(R.id.item_track_info)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_menu_list -> {
                toggleMenuOpenClose()
            }
            R.id.item_pic_in_pic -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (hasPicInPicPermission())
                        try {
                            enterPicInPic()
                        } catch (e: Exception) {
                            toast("Cannot Entre PicInPic due Unknown error")
                            Log.e("ExoPlayerActivity", "onOptionsItemSelected: ", e)
                        }
                    else {
                        showPicInPicPermissionDialog()
                    }
                }
            }
            R.id.item_track_info -> {
                if (TrackSelectionDialog.willHaveContent(player!!.trackSelector))
                    TrackSelectionDialog.createForTrackSelector(player!!.trackSelector) {
                    }.show(supportFragmentManager, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enterPicInPic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                with(PictureInPictureParams.Builder()) {
                    setAspectRatio(getPipRatio())
                    build()
                })
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            enterPictureInPictureMode()
        }
    }

    private fun showPicInPicPermissionDialog() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_permission_picture_in_picture)
            .setTitle(getString(R.string.permissions_needed))
            .setMessage(R.string.picture_in_picture_disabled_message)
            .setPositiveButton(getString(R.string.enable)) { _: DialogInterface, _: Int ->
                try {
                    startActivity(
                        Intent(
                            "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                            Uri.parse("package:$packageName")
                        )
                    )
                } catch (e: Exception) {
                    toast(getString(R.string.pic_in_pic_not_supported))
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
            }.show()
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        player = null
    }


    // ExoPlayer related functions.
    private fun createPlayer() {
        player = PlayerImpl(
            this,
            playerState,
            view.playerView,
            this,
            castContext!!
        )


         view.playerView.setControllerVisibilityListener { visibility ->
             view.playerToolbar.toggleVisibilityWithAnimation(visibility == View.VISIBLE)
         }

        view.root.findViewById<AppCompatImageButton>(R.id.btn_rotate_screen).setOnClickListener {
            toggleRotateScreen()
        }
        view.channelsSearchView.setIconifiedByDefault(false)
        view.channelsSearchView.setOnQueryTextListener(this)
    }

    private fun toggleRotateScreen() {
        requestedOrientation = when (resources.configuration.orientation) {
            ORIENTATION_LANDSCAPE -> {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            else -> {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    /* Yodo1
    override fun onPlayerError(error: ExoPlaybackException) {
        onError(error)
    } */

    open fun onError(exception: Exception) {
        view.videoLoadingProgress.isVisible = false
        view.playerErrorText.isVisible = true
        if (!isNetworkAvailable()) {
            view.playerErrorText.text = getString(R.string.player_network_error_message)
        } else {
            view.playerErrorText.text = exception.message ?: "There is unknown error!"
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun startPlayer(url: String) {
        closePlayerMenu()
        player?.start(url)
        toggleResizeMode(currentResizeMode)
    }

    protected fun stopPlayer() {
        player?.stop()
    }

    private fun releasePlayer() {
        player?.release()
    }

    // Picture in Picture related functions.
    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (hasPicInPicPermission())
                try {
                    enterPicInPic()
                } catch (e: Exception) {
                    Log.e("ExoPlayerActivity", "onOptionsItemSelected: ", e)
                    finish()
                }
            else {
                showPicInPicPermissionDialog()
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        view.playerView.useController = !isInPictureInPictureMode
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }


    companion object {
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"

        //        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private const val KEY_RESIZE_MODE = "resize_mode"
        private val sizeModes = mapOf(
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH to R.drawable.ic_player_mode_fullscreen,
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT to R.drawable.ic_player_mode_center_focus,
            AspectRatioFrameLayout.RESIZE_MODE_FILL to R.drawable.ic_player_mode_zoom_out,
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM to R.drawable.ic_player_mode_center_focus,
            AspectRatioFrameLayout.RESIZE_MODE_FIT to R.drawable.ic_player_mode_fit
        )
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isInPictureInPictureMode) {
                player?.stop()
            }
        } else {
            player?.stop()
        }

    }

    open fun getPipRatio(): Rational {
        val width = window.decorView.width
        val height = window.decorView.height
        return Rational(height, width)
    }

    private fun hasPicInPicPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager?
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appOps?.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                } else {
                    @Suppress("DEPRECATION")
                    appOps?.checkOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}