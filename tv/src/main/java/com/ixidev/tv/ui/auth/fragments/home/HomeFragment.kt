package com.ixidev.tv.ui.auth.fragments.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.tv.BuildConfig
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val rootView: FragmentHomeBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView.btnLoadFile.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_play_list)
        }
        rootView.btnAllUsers.setOnClickListener {
            findNavController().navigate(R.id.navigation_all_play_lists)
        }
        rootView.btnLoginXtream.setOnClickListener {
            findNavController().navigate(R.id.navigation_login_xtream_codes)
        }
    }
}