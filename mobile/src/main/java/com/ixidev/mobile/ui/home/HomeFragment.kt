package com.ixidev.mobile.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.adstoolkit.core.IBannerAd
import com.ixidev.adstoolkit.core.INativeAd
import com.ixidev.data.common.EncryptionUtils.decrypt
import com.ixidev.data.logger.EVENT_CHANNEL_CLICK
import com.ixidev.data.logger.EVENT_EXTRACTS_OPEN_FROM
import com.ixidev.data.logger.EVENT_SELECT_TAG
import com.ixidev.data.logger.IAppLogger
import com.ixidev.data.model.MovieItem
import com.ixidev.mobile.R
import com.ixidev.mobile.ads.FbNativeAdAdapter
import com.ixidev.mobile.databinding.HomeFragmentBinding
import com.ixidev.mobile.ui.common.*
import com.ixidev.mobile.ui.main.MainViewModel
import com.ixidev.mobile.ui.player.MobilePlayerActivity
import com.ixidev.smarttvapp.ads.HomeBannerId
import com.ixidev.smarttvapp.ads.HomeNativeAdId
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.yodo1.mas.Yodo1Mas
import com.yodo1.mas.error.Yodo1MasError
import com.yodo1.mas.event.Yodo1MasAdEvent
import com.yodo1.mas.nativeads.Yodo1MasNativeAdListener
import com.yodo1.mas.nativeads.Yodo1MasNativeAdView

@AndroidEntryPoint
class HomeFragment : SearchableFragment(R.layout.home_fragment) {

    private val viewModel: MainViewModel by activityViewModels()
    private val homeBinding: HomeFragmentBinding by viewBinding()

    //@Inject
    //lateinit var bannerAd: IBannerAd

    @Inject
    lateinit var nativeAd: INativeAd

    @Inject
    lateinit var logger: IAppLogger

    //@Inject
   // @HomeBannerId
   // lateinit var bannerId: String

    @Inject
    @HomeNativeAdId
    lateinit var nativeAdId: String

    private val adapter: ChannelsAdapter by lazy {
        FbNativeAdAdapter(
            movieClickListener = {
                logger.logEvent(
                    EVENT_CHANNEL_CLICK, bundleOf(
                        EVENT_EXTRACTS_OPEN_FROM to "Home"
                    )
                )
                MobilePlayerActivity.start(
                    requireContext(),
                    it.sourceUrl!!,
                    it.title,
                    it.categorie,
                    it.listId
                )
            },
            movieFavChange = {
                viewModel.changeChannelItemFav(it)
            }
        )
    }
    private val tagsAdapter: Adapter by lazy {
        Adapter(
            tagLayoutId = R.layout.tag_item_layout,
            tagTextViewId = R.id.tag_text_view,
            onTagSelected = { category, _ ->
                logger.logEvent(EVENT_SELECT_TAG)
                if (category == "All")
                    viewModel.setSelectedCategory(null)
                else
                    viewModel.setSelectedCategory(category)
            }
        )
    }

    private val loadState: (state: CombinedLoadStates) -> Unit by lazy {
        {
            val hasData = adapter.itemCount > 0
            homeBinding.homeRecyclerView.isVisible = hasData
            homeBinding.homeTagsView.isVisible = hasData
            homeBinding.homText.isVisible = !hasData
            if (hasData && !nativeAdInitialized) {
              // Yodo1  initNativeAd()
            }
        }
    }

    override fun onSearch(query: String?) {
        viewModel.onSearch(query)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding.homeRecyclerView.layoutManager = createLayoutManager()
        homeBinding.homeRecyclerView.adapter = adapter
        homeBinding.homeTagsView.setAdapter(tagsAdapter)
        adapter.addLoadStateListener(loadState)

        viewModel.playListChannels.observe(viewLifecycleOwner) {
            it?.let {
                onChannelsListChange(addNativeAdsItems(it))
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            onCategoriesChange(it)
        }
       /* lateinit var nativeAdView : Yodo1MasNativeAdView
        nativeAdView = view.findViewById(R.id.yodo1_mas_native_1)
        nativeAdView.setAdListener(object: Yodo1MasNativeAdListener {
            override fun onNativeAdLoaded(nativeAdView: Yodo1MasNativeAdView? ) {
                // Code to be executed when an ad finishes loading.
            }
            override fun onNativeAdFailedToLoad(nativeAdView: Yodo1MasNativeAdView?, error : Yodo1MasError) {
                // Code to be executed when an ad request fails.
            }
        })
        nativeAdView.loadAd()*/
        if (SaveSharedPreference.showAds(requireContext())) {
            initAds()
        }

    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        val manager = GridLayoutManager(context, 3)
        manager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adapter.getItemViewType(position) == AbstractChannelsWithNativeAdAdapter.NATIVE_AD_VIEW_TYPE) {
                    return manager.spanCount // native ad view will take all span count ( full width )
                }
                return 1
            }
        }
        return manager
    }

    private fun addNativeAdsItems(data: PagingData<MovieItem>): PagingData<MovieItem> {
        // insert native ad after 15 items
        return data.insertSeparators { _, after ->
            if (after != null && after.id % 15 == 0 && SaveSharedPreference.showAds(requireContext())) {
                MovieItem(id = -1, listId = -1)
            } else {
                null
            }
        }
    }

    private fun onCategoriesChange(cats: List<String>?) {
        if (cats.isNullOrEmpty()) {
            homeBinding.homeTagsView.clear()
        } else {
            val tags = ArrayList<String>()
            tags.add("All")
            tags.addAll(cats)
            homeBinding.homeTagsView.setTags(tags)
        }
    }

    private fun onChannelsListChange(channels: PagingData<MovieItem>) {
        adapter.submitData(lifecycle, channels)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeLoadStateListener(loadState)
        viewModel.playListChannels.removeObservers(viewLifecycleOwner)
        viewModel.categories.removeObservers(viewLifecycleOwner)
        homeBinding.homeRecyclerView.adapter = null
        homeBinding.homeRecyclerView.removeAllViews()
        homeBinding.homeTagsView.clear()
        homeBinding.homeTagsView.removeAllViews()
      // Yodo1 bannerAd.destroy()
        nativeAd.destroy()
        homeBinding.mobileHomeBanner.removeAllViews()
    }

    // Yodo1

    private fun initBanner(){
        val bannerListener: Yodo1Mas.BannerListener = object : Yodo1Mas.BannerListener() {
            override fun onAdOpened(event: Yodo1MasAdEvent) {

            }
            override fun onAdError(event: Yodo1MasAdEvent, error: Yodo1MasError) {
                //Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
            override fun onAdClosed(event: Yodo1MasAdEvent) {

            }
        }
        Yodo1Mas.getInstance().setBannerListener(bannerListener)
    }

    // end Yodo1

    override fun onPause() {
        super.onPause()
      // Yodo1 bannerAd.onPause()
        if (SaveSharedPreference.showAds(requireContext())) {
            Yodo1Mas.getInstance().dismissBannerAd() // Yodo1
        }
    }

    override fun onResume() {
        super.onResume()
      // Yodo1 bannerAd.onResume()

        if (SaveSharedPreference.showAds(requireContext())) {
            Yodo1Mas.getInstance().showBannerAd(requireActivity()) // Yodo1
        }
    }

    private fun initAds() {
        /* Yodo1
        bannerAd.load(requireContext(), bannerId.decrypt())
        bannerAd.render(homeBinding.mobileHomeBanner)
        (adapter as? FbNativeAdAdapter)?.setNativeAdItem(nativeAd) */
            initBanner() // Yodo1
            initNativeAd()

        }

    private var nativeAdInitialized = false
    private fun initNativeAd() {
        nativeAdInitialized = true
        //nativeAd.load(requireContext(), nativeAdId.decrypt())
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "HomeFragment"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_play_list -> {
               findNavController().navigate(R.id.playListsFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}