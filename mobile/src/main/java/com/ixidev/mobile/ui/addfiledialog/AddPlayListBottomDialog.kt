package com.ixidev.mobile.ui.addfiledialog

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.developer.filepicker.view.FilePickerDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ixidev.data.common.getFileName
import com.ixidev.data.common.toast
import com.ixidev.data.logger.EVENT_OPEN_FILE_BROWSER
import com.ixidev.data.logger.EVENT_OPEN_URL_DIALOG
import com.ixidev.data.logger.EVENT_OPEN_XTREAM_DIALOG
import com.ixidev.data.logger.IAppLogger
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.AddPlaylistDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "AddPlayListBottomDialog"

@AndroidEntryPoint
class AddPlayListBottomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: AddPlaylistDialogFragmentBinding

    @Inject
    lateinit var filePickerDialog: FilePickerDialog

    @Inject
    lateinit var logger: IAppLogger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddPlaylistDialogFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addFromUrl.setOnClickListener {
            findNavController().popBackStack()
            logger.logEvent(EVENT_OPEN_URL_DIALOG)
            findNavController().navigate(R.id.addM3uUrlDialog)
        }
        binding.addXtream.setOnClickListener {
            findNavController().popBackStack()
            logger.logEvent(EVENT_OPEN_XTREAM_DIALOG)
            findNavController().navigate(R.id.addXtreamAccountDialog)
        }

        binding.addFromFile.setOnClickListener {
            pickFileFromStorage()
        }
    }

    private fun parseFile(path: String) {

        val playList = MoviesPlayList(
            id = 0,
            name = File(path).name,
            path
        )
        findNavController().navigate(
            R.id.actionParseUri,
            Bundle().apply {
                putSerializable("playList", playList)
            }
        )
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                try {
                    val fileName = uri.getFileName()
                    val file = File(requireActivity().cacheDir, fileName)
                    val out = FileOutputStream(file)
                    requireActivity().contentResolver.openInputStream(uri)?.copyTo(out)
                    parseFile(file.path)
                } catch (e: Exception) {
                    toast("Cannot open this file !")
                    Log.e(TAG, "registerForActivityResult: ", e)
                }
            }
        }

    private fun pickFileFromStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getContent.launch("audio/x-mpegurl")
        } else {
            filePickerDialog.setDialogSelectionListener {
                it?.first()?.let { path ->
                    parseFile(path)
                }
            }
            logger.logEvent(EVENT_OPEN_FILE_BROWSER)
            filePickerDialog.show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d = it as? BottomSheetDialog
            val bottomSheet = d?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
            }
        }
        return dialog
    }
}
