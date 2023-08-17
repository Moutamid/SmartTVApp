package com.ixidev.smarttvapp.ads

import com.google.android.gms.ads.AdRequest
import com.ixidev.adstoolkit.admob.RecyclerViewAdmobNativeAd
import com.ixidev.adstoolkit.admob.SimpleAdMobBanner
import com.ixidev.adstoolkit.admob.SimpleAdMobInterstitial
import com.ixidev.adstoolkit.core.IBannerAd
import com.ixidev.adstoolkit.core.IInterstitialAd
import com.ixidev.adstoolkit.core.INativeAd
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Qualifier

/**
 * Created by ABDELMAJID ID ALI on 2/2/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@InstallIn(ActivityComponent::class)
@Module
object AdsModule {


    @Provides
    fun provideBannerAd(adRequest: AdRequest.Builder): IBannerAd {
        return SimpleAdMobBanner(adRequest)
    }

    @Provides
    fun provideInterstitialAd(adRequest: AdRequest.Builder): IInterstitialAd {
        return SimpleAdMobInterstitial(adRequest)
    }

    @Provides
    fun provideNativeAd(adRequest: AdRequest.Builder): INativeAd {
        return RecyclerViewAdmobNativeAd(adRequest)
    }

    @Provides
    @HomeBannerId
    fun provideHomeBannerId(): String {
        return BuildConfig.HOME_BANNER_AD_ID
    }

    @Provides
    @HomeNativeAdId
    fun provideHomeNativeAdId(): String {
        return BuildConfig.HOME_NATIVE_AD_ID
    }

    @Provides
    @AddFileInterstitialAdId
    fun provideAddFileInterstitialAdId(): String {
        return BuildConfig.ADD_FILE_INTERSTITIAL_AD_ID
    }

    @Provides
    @PlayerInterstitialAdId
    fun providePlayerInterstitialAdId(): String {
        return BuildConfig.PLAYER_INTERSTITIAL_AD_ID
    }

    @Provides
    fun provideAdRequest(): AdRequest.Builder {
        return AdRequest.Builder()
    }
}


@Qualifier
annotation class HomeBannerId

@Qualifier
annotation class HomeNativeAdId

@Qualifier
annotation class AddFileInterstitialAdId

@Qualifier
annotation class PlayerInterstitialAdId

