package com.ixidev.mobile.ui.common

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.adstoolkit.core.INativeAd
import com.ixidev.mobile.databinding.NativeAdItemBinding
import com.yodo1.mas.error.Yodo1MasError
import com.yodo1.mas.nativeads.Yodo1MasNativeAdListener
import com.yodo1.mas.nativeads.Yodo1MasNativeAdView

/**
 * Created by ABDELMAJID ID ALI on 2/4/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

abstract class AbstractChannelsWithNativeAdAdapter<AdViewHolder : AbstractChannelsWithNativeAdAdapter.NativeAdViewHolder>(
    movieClickListener: onMovieClickListener,
    movieFavChange: onMovieFavChange
) : ChannelsAdapter(movieClickListener, movieFavChange) {

    private val nativeAdListener: Yodo1MasNativeAdListener = object : Yodo1MasNativeAdListener {
        override fun onNativeAdLoaded(view: Yodo1MasNativeAdView?) {
            Log.d("native Loaded", "Yes")
            // call timer here
        }

        override fun onNativeAdFailedToLoad(view: Yodo1MasNativeAdView?, error: Yodo1MasError?) {
            //loadSmartBannerAd()
        }

    }

    private var nativeAd: INativeAd? = null

    fun setNativeAdItem(ad: INativeAd) {
        this.nativeAd = ad
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            if ((it.id == -1) && (it.listId == -1))
                return NATIVE_AD_VIEW_TYPE
        }
        return ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NATIVE_AD_VIEW_TYPE)
            return NativeAdViewHolder(NativeAdItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return super.onCreateViewHolder(parent, viewType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NATIVE_AD_VIEW_TYPE) {
            val holder = holder1 as NativeAdViewHolder
            holder.bindingView.apply {
                yodo1MasNative.setAdListener(nativeAdListener)
                yodo1MasNative.loadAd()
            }
            //renderNativeAd(nativeAd, holder as AdViewHolder)
        } else
            super.onBindViewHolder(holder1, position)
    }

    private fun renderNativeAd(ad: INativeAd?, holder: AdViewHolder) {
        ad?.render(holder.itemView as FrameLayout)
    }

    abstract fun createAdViewHolder(parent: ViewGroup): AdViewHolder

    open class NativeAdViewHolder(val bindingView: NativeAdItemBinding) :
        RecyclerView.ViewHolder(bindingView.root) {
    }

    companion object {
        const val NATIVE_AD_VIEW_TYPE = -1
        const val ITEM_VIEW_TYPE = 0
    }
}