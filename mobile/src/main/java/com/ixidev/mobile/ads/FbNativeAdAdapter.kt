package com.ixidev.mobile.ads

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.NativeAdItemBinding
import com.ixidev.mobile.ui.common.AbstractChannelsWithNativeAdAdapter
import com.ixidev.mobile.ui.common.onMovieClickListener
import com.ixidev.mobile.ui.common.onMovieFavChange

/**
 * Created by ABDELMAJID ID ALI on 2/4/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class FbNativeAdAdapter(
    movieClickListener: onMovieClickListener,
    movieFavChange: onMovieFavChange
) : AbstractChannelsWithNativeAdAdapter<FbNativeAdAdapter.FbNativeAdHolder>(
    movieClickListener,
    movieFavChange
) {


    class FbNativeAdHolder(bindingView: NativeAdItemBinding) : AbstractChannelsWithNativeAdAdapter.NativeAdViewHolder(bindingView)

    override fun createAdViewHolder(parent: ViewGroup): FbNativeAdHolder {
        TODO("Not yet implemented")
    }

    /*override fun createAdViewHolder(parent: ViewGroup): FbNativeAdHolder {
        return FbNativeAdHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.home_native_ad_layout,
                parent, false
            ) as FrameLayout

        )

    }*/
}