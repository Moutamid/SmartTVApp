package com.ixidev.mobile.ui.favoriets

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.logger.EVENT_CHANNEL_CLICK
import com.ixidev.data.logger.EVENT_EXTRACTS_OPEN_FROM
import com.ixidev.data.logger.IAppLogger
import com.ixidev.data.model.MovieItem
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.FavoritesFragmentBinding
import com.ixidev.mobile.ui.common.ChannelsAdapter
import com.ixidev.mobile.ui.player.MobilePlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.favorites_fragment) {
    private val viewModel: FavoritesViewModel by viewModels()
    private val viewBinding: FavoritesFragmentBinding by viewBinding()

    @Inject
    lateinit var logger: IAppLogger

    private val adapter: ChannelsAdapter by lazy {
        ChannelsAdapter(
            movieClickListener = {
                logger.logEvent(
                    EVENT_CHANNEL_CLICK, bundleOf(
                        EVENT_EXTRACTS_OPEN_FROM to "favorites"
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

    private val loadState: (state: CombinedLoadStates) -> Unit by lazy {
        {
            val hasData = adapter.itemCount > 0
            viewBinding.favoritesRecyclerView.isVisible = hasData
            viewBinding.favText.isVisible = !hasData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.favoritesRecyclerView.adapter = adapter
        adapter.addLoadStateListener(loadState)

        viewModel.getFavorites().observe(viewLifecycleOwner) {
            onChannelsListChange(it)
        }
    }


    private fun onChannelsListChange(channels: PagingData<MovieItem>) {
        adapter.submitData(lifecycle, channels)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeLoadStateListener(loadState)
        viewModel.getFavorites().removeObservers(viewLifecycleOwner)
        viewBinding.favoritesRecyclerView.adapter = null
        viewBinding.favoritesRecyclerView.removeAllViews()
    }
}