package com.ixidev.mobile.ui.addfiledialog

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.AddM3uUrlDialogFragmentBinding

class AddM3uUrlDialog : BottomSheetDialogFragment() {

    private lateinit var binding: AddM3uUrlDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddM3uUrlDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSaveUrl.setOnClickListener {
            onDownloadUrl(
                binding.m3uUrlName.text.toString(),
                binding.m3uUrlInput.text.toString()
            )
        }
    }

    private fun onDownloadUrl(name: String, url: String) {

        if (name.isEmpty()) {
            binding.m3uUrlName.error = getString(R.string.any_nam_error)
            binding.m3uUrlName.requestFocus()
            return
        }
        if (url.isEmpty() || !Patterns.WEB_URL.matcher(url).matches()) {
            binding.m3uUrlInput.error = getString(R.string.m3u_url_error)
            binding.m3uUrlInput.requestFocus()
            return
        }
        val playList = MoviesPlayList(
            id = 0,
            name = name,
            url
        )
        dismiss()
        findNavController().navigate(
            R.id.actionParseUri,
            Bundle().apply {
                putSerializable("playList", playList)
            }
        )
    }

}