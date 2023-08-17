package com.ixidev.tv.ui.fileparser

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.FileParserViewModel
import com.ixidev.data.common.AppTask
import com.ixidev.data.common.args
import com.ixidev.data.common.toast
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.tv.R
import com.ixidev.tv.common.showError
import com.ixidev.tv.databinding.FragmentFileParserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FileParserFragment : Fragment(R.layout.fragment_file_parser) {

    private val viewModel: FileParserViewModel by viewModels()
    private val rootView: FragmentFileParserBinding by viewBinding()
    private val playPlayList: MoviesPlayList? by args(ARG_PLAYLIST)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (playPlayList == null) {
            showError("Invalid File or url")
            return
        }

        viewModel.parsFilesTask.observe(viewLifecycleOwner, ::observeLoadFileTask)
        viewModel.parsePlayList(playPlayList!!)
    }

    private fun observeLoadFileTask(task: AppTask<*>) {
        when (task) {
            is AppTask.Error -> {
                Log.e(TAG, "LoadFileTask: error :", task.error)
                showError(task.error)
            }
            is AppTask.Success -> {
                toast("(${task.data} items loaded successfully)")
                findNavController().navigate(R.id.action_show_play_lists)
                onDestroy()
            }
            is AppTask.Loading -> {
                showLoadingMessage(task.message)
            }
            else -> {
                // progressing
            }
        }
    }

    private fun showLoadingMessage(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            rootView.loadingMessage.text = message
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "FileParserFragment"
        const val ARG_PLAYLIST = "playlist"
    }
}