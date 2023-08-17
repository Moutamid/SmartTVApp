package com.ixidev.mobile.ui.playlists

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.logger.EVENT_SELECT_DELETE_PLAYLIST
import com.ixidev.data.logger.EVENT_SELECT_PLAYLIST
import com.ixidev.data.logger.IAppLogger
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.PlayListsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PlayListsFragment : Fragment(R.layout.play_lists_fragment) {

    private val viewModel: PlayListsViewModel by viewModels()

    private val binding: PlayListsFragmentBinding by viewBinding()

    @Inject
    lateinit var logger: IAppLogger

    private val adapter: MobilePlayListsAdapter by lazy {
        MobilePlayListsAdapter(
            ::onPlaylistClick,
            ::onPlayListSelected,
            ::onEnableSelectionMode,
            ::isItemSelected,
            ::onItemRefreshClick
        )
    }

    private var actionMode: android.view.ActionMode? = null

    private fun onEnableSelectionMode() {
        viewModel.setSelectionModeEnabled(true)
    }

    private fun onPlayListSelected(playlistId: Int, isSelected: Boolean) {
        viewModel.onPlayListSelectionChange(playlistId, isSelected)
    }

    private fun isItemSelected(playListId: Int): Boolean {
        return viewModel.isPlayListSelected(playListId)
    }

    private fun onItemRefreshClick(moviesPlayList: MoviesPlayList) {
        val playList = MoviesPlayList(
            id = 0,
            name = moviesPlayList.name,
            moviesPlayList.fileUrl
        )
        findNavController().navigate(
            R.id.actionParseUri,
            Bundle().apply {
                putSerializable("playList", playList)
            }
        )
         viewModel.deleteRefreshedItem(moviesPlayList.id)
    }

    private val loadState: (state: CombinedLoadStates) -> Unit by lazy {
        {
            val hasData = adapter.itemCount > 0
            binding.recyclerView.isVisible = hasData
            binding.playListText.isVisible = !hasData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(
                R.id.addPlayListBottomDialog
            )
        }
        adapter.addLoadStateListener(loadState)
        initData()
    }

    private fun initData() {

        viewModel.getAllPlayLists().observe(viewLifecycleOwner) {
            onLoadPlayLists(it)
        }
        viewModel.selectionMode.observe(viewLifecycleOwner) { isSelectionMode ->
            isSelectionMode?.let {
                toggleActionMode(isSelectionMode)
            }
        }
        viewModel.selectedItemsCount.observe(viewLifecycleOwner) { count ->
            actionMode?.title = "$count"
        }
    }

    private fun toggleActionMode(selectionMode: Boolean) {

        adapter.enableSelectionMode(selectionMode)

        if (selectionMode) {
            this.actionMode = requireActivity().startActionMode(
                PlayListSelectionCallBack(
                    ::onDeleteSelectedItems
                )
            )
            // set listener for action mode close button
            requireActivity().findViewById<View>(R.id.action_mode_close_button)
                ?.setOnClickListener {
                    viewModel.setSelectionModeEnabled(false)
                }
        } else {
            requireActivity()
                .findViewById<View>(R.id.action_mode_close_button)?.setOnClickListener(null)
            actionMode?.finish()
            actionMode = null
        }

    }

    private fun onDeleteSelectedItems() {
        AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_Dialog).apply {
            setTitle("Delete Playlists")
            setMessage("Delete all selected playlists and channels !")
            setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                viewModel.deleteSelectedItems()
                logger.logEvent(EVENT_SELECT_DELETE_PLAYLIST)
            }
            setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            }
            setIcon(R.drawable.ic_action_mode_warnning)
        }.show()
    }

    private fun onLoadPlayLists(data: PagingData<MoviesPlayList>) {
        adapter.submitData(lifecycle, data)
    }


    private fun onPlaylistClick(id: Int) {
        logger.logEvent(EVENT_SELECT_PLAYLIST)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.setCurrentPlayList(id)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeLoadStateListener(loadState)
        viewModel.getAllPlayLists().removeObservers(viewLifecycleOwner)
        binding.recyclerView.adapter = null
        binding.recyclerView.removeAllViews()
        actionMode?.finish()
        actionMode = null
    }

}