package com.ixidev.tv.ui.auth.fragments.loadfile

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.common.toText
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentXtreamAccountLoginBinding
import com.ixidev.tv.ui.fileparser.FileParserFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadXtreamAccountFragment : Fragment(R.layout.fragment_xtream_account_login) {

    private val rootView: FragmentXtreamAccountLoginBinding by viewBinding()
    private val viewModel: FileUrlViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView.btnAddXtreamAccount.setOnClickListener {
            onLogin()
        }
    }

    private fun onLogin() {
        if (inputNotValid()) {
            return
        }
        val name = rootView.tvAnyName.toText()
        val username = rootView.tvUserName.toText()
        val password = rootView.tvPassword.toText()
        val url = rootView.tvXtreamUrl.toText()

        val tmpUrl = if (url.endsWith("/")) url else url.removeSuffix("/")
        val playListUrl = "$tmpUrl/get.php?username=$username&password=$password&type=m3u_plus"

        findNavController().navigate(
            R.id.fileParserFragment,
            Bundle().also {
                it.putSerializable(
                    FileParserFragment.ARG_PLAYLIST,
                    MoviesPlayList(0, name, playListUrl)
                )
            }
        )
    }



    private fun inputNotValid(): Boolean {

        var tvInput: EditText? = null
        var errorMessage = ""

        val url = rootView.tvXtreamUrl.toText()
        if (url.isEmpty() && !viewModel.isXtreamUrlValid(url)) {
            tvInput = rootView.tvXtreamUrl
            errorMessage = getString(R.string.xtream_url_error_message)
        }
        if (rootView.tvPassword.toText().isEmpty()) {
            tvInput = rootView.tvPassword
            errorMessage = getString(R.string.password_error_message)
        }

        if (rootView.tvUserName.toText().isEmpty()) {
            tvInput = rootView.tvUserName
            errorMessage = getString(R.string.username_error_message)
        }

        if (rootView.tvAnyName.text.toString().isEmpty()) {
            tvInput = rootView.tvAnyName
            errorMessage = getString(R.string.playlist_name_error_message)
        }

        tvInput?.let {
            it.error = errorMessage
            it.requestFocus()
            return true
        }
        return false
    }


}