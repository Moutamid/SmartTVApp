package com.ixidev.tv.ui.auth.fragments.loadfile

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.developer.filepicker.view.FilePickerDialog
import com.ixidev.data.common.*
import com.ixidev.data.logger.EVENT_OPEN_FILE_BROWSER
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentLoadFileUrlBinding
import com.ixidev.tv.ui.fileparser.FileParserFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@Suppress("unused")
private const val TAG = "LoadFileAndUrlFragment"

@AndroidEntryPoint
class LoadFileAndUrlFragment : Fragment(R.layout.fragment_load_file_url) {

    private val rootView: FragmentLoadFileUrlBinding by viewBinding()
    private val viewModel: FileUrlViewModel by viewModels()
    private var selectedFilePath: String? = null

    @Inject
    lateinit var fileBrowser: FilePickerDialog

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                try {
                    val fileName = uri.getFileName()
                    val file = File(requireActivity().cacheDir, fileName)
                    val out = FileOutputStream(file)
                    requireActivity().contentResolver.openInputStream(uri)?.copyTo(out)
                    onFileSelected(file.path)
                } catch (e: Exception) {
                    toast("Cannot open this file !")
                    Log.e(TAG, "registerForActivityResult: ", e)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView.radioButtonsGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radbtnFile -> {
                    rootView.tvM3uUrl.invisible()
                    rootView.btnBrowserFile.show()
                    rootView.tvSelectedFilePath.show()
                    rootView.tvSelectedFilePath.text = ""
                    this.selectedFilePath = null
                }
                R.id.radBtnUrl -> {
                    rootView.tvM3uUrl.show()
                    rootView.btnBrowserFile.invisible()
                    rootView.tvSelectedFilePath.hide()
                }
            }
        }
        rootView.btnBrowserFile.setOnClickListener {
            openFileBrowser()
        }
        rootView.btnAddPlylist.setOnClickListener {
            onAddPlayList()
        }
        rootView.btnAllPlylists.setOnClickListener {
            findNavController().navigate(R.id.action_show_play_lists)
        }

    }

    private fun onAddPlayList() {
        if (inputValidNotValid())
            return
        val playListName = rootView.tvPlaylistName.toText()
        val playListUrl: String? = when {
            rootView.radbtnFile.isChecked -> {
                selectedFilePath
            }
            rootView.radBtnUrl.isChecked -> {
                rootView.tvM3uUrl.toText()
            }
            else -> {
                null
            }
        }

        playListUrl?.let { url ->
            findNavController().navigate(
                R.id.fileParserFragment,
                Bundle().also {
                    it.putSerializable(
                        FileParserFragment.ARG_PLAYLIST,
                        MoviesPlayList(0, playListName, url)
                    )
                }
            )
        }
    }

    private fun inputValidNotValid(): Boolean {
        // remove error messages
        with(rootView) {
            tvSelectedFilePath.error = null
            tvM3uUrl.error = null
            tvPlaylistName.error = null
            btnBrowserFile.error = null
        }

        if (rootView.tvPlaylistName.text.toString().isEmpty()) {
            with(rootView.tvPlaylistName) {
                error = context.getString(R.string.playlist_name_error_message)
                requestFocus()
            }
            return true
        }
        if (rootView.radbtnFile.isChecked && selectedFilePath.isNullOrEmpty()) {
            with(rootView.btnBrowserFile) {
                error = context.getString(R.string.select_file_error_message)
                requestFocus()
            }
            return true
        }
        if (rootView.radBtnUrl.isChecked) {
            val isUrlValid = viewModel.isM3uUrlValid(rootView.tvM3uUrl.toText())
            if (!isUrlValid)
                with(rootView.tvM3uUrl) {
                    error = context.getString(R.string.m3u_url_error_message)
                    requestFocus()
                }
            return !isUrlValid
        }
        return false
    }

    private fun openFileBrowser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getContent.launch("audio/x-mpegurl")
        } else {
            fileBrowser.setDialogSelectionListener {
                if (it.isNotEmpty()) {
                    onFileSelected(it[0])
                }
            }
            fileBrowser.show()
        }
    }

    private fun onFileSelected(path: String?) {
        if (path.isNullOrEmpty()) {
            toast(getString(R.string.file_path_empty_error_massage))
            return
        }
        this.selectedFilePath = path
        rootView.tvSelectedFilePath.text = path
    }
}