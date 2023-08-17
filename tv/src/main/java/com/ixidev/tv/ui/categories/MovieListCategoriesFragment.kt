package com.ixidev.tv.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ixidev.data.common.args
import com.ixidev.tv.R
import com.ixidev.tv.databinding.FragmentMovieListCategoriesBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ABDELMAJID ID ALI on 05/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@AndroidEntryPoint
class MovieListCategoriesFragment : Fragment(R.layout.fragment_movie_list_categories) {
    private val listId: Int by args()
    private val viewModel: MovieListCategoriesViewModel by viewModels()
    private val rootView: FragmentMovieListCategoriesBinding by viewBinding()
    private var adapter: CategoriesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoriesAdapter { category ->
            findNavController().navigate(R.id.navigation_listMoviesFragment, Bundle().apply {
                putInt("listId", listId)
                putString("category", category)
            })
        }
        rootView.recylerView.adapter = adapter
        viewModel.getMovieListCategories().observe(viewLifecycleOwner) {
            adapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

}