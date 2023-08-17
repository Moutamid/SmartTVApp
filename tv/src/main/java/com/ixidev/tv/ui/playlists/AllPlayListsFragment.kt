package com.ixidev.tv.ui.playlists

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentAllPlayListsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPlayListsFragment : Fragment(R.layout.fragment_all_play_lists) {

    private val viewModel: AllPlayListsViewModel by viewModels()
    private val rootView: FragmentAllPlayListsBinding by viewBinding()
    private var adapter: PlayListsAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlayListsAdapter { playlistId ->

            findNavController().navigate(R.id.movieListCategoriesFragment, Bundle().apply {
                putInt("listId", playlistId)
            })
        }
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        rootView.playListsRecyclerView.adapter = adapter
        rootView.btnAddPlayList.setOnClickListener {
            findNavController().navigate(R.id.action_add_play_list)
        }

        viewModel.getMoviesList().observe(viewLifecycleOwner) {
            it?.let {
                adapter?.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

}