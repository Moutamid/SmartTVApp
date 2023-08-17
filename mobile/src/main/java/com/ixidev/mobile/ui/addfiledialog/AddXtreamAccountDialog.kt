package com.ixidev.mobile.ui.addfiledialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ixidev.data.common.isUrl
import com.ixidev.data.common.toText
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.AddXtreamDialogFragmentBinding

class AddXtreamAccountDialog : BottomSheetDialogFragment() {

    private lateinit var binding: AddXtreamDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddXtreamDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLoginXtream.setOnClickListener {
            val name = binding.xtreamAnyName.toText()
            val username = binding.xtreamUserName.toText()
            val password = binding.xtreamPassword.toText()
            val url = binding.xtreamAccountUrl.toText()
            if (inputValid(name, username, password, url)) {
                onXtreamLogin(name, username, password, url)
            }
        }
    }

    private fun onXtreamLogin(name: String, username: String, password: String, url: String) {
        val tmpUrl = if (url.endsWith("/")) url else url.removeSuffix("/")
        val playListUrl = "$tmpUrl/get.php?username=$username&password=$password&output=m3u8&type=m3u_plus"
        dismiss()
        findNavController().navigate(
            R.id.playListParserActivity,
            Bundle().apply {
                putSerializable(
                    "playList",
                    MoviesPlayList(0, name, playListUrl, false)
                )
            }
        )
    }


    private fun inputValid(name: String, username: String, password: String, url: String): Boolean {

        val (editText, errorMessage) = when {
            name.isEmpty() -> {
                binding.xtreamAnyName to getString(R.string.any_nam_error)
            }
            !url.isUrl() -> {
                binding.xtreamAccountUrl to getString(R.string.xtream_url_error)
            }
            username.isEmpty() -> {
                binding.xtreamUserName to getString(R.string.usern_name_error)
            }
            password.isEmpty() -> {
                binding.xtreamPassword to getString(R.string.xtream_password_error)
            }

            else -> null to null
        }

        editText?.let {
            it.error = errorMessage
            it.requestFocus()
            return false
        }
        return true
    }
}