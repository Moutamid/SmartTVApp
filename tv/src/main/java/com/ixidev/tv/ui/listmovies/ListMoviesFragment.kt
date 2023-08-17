package com.ixidev.tv.ui.listmovies

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.common.args
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentListMoviesBinding
import com.ixidev.tv.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ABDELMAJID ID ALI on 06/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@AndroidEntryPoint
class ListMoviesFragment : Fragment(R.layout.fragment_list_movies) {

    private val rootView: FragmentListMoviesBinding by viewBinding()
    private val viewModel: ListMoviesViewModel by viewModels()
    private var adapter: ListMoviesAdapter? = null
    private val category: String? by args()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListMoviesAdapter {
            PlayerActivity.start(requireContext(), it.title, it.sourceUrl)
        }
        rootView.moviesRecycler.adapter = adapter
        rootView.toolbarLayout.toolbarTitle.text = category ?: ""
        viewModel.getCategoryMovies().observe(viewLifecycleOwner) {
            adapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

}

