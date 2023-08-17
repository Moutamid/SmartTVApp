package com.ixidev.mobile.ui.common

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.ixidev.mobile.R

abstract class SearchableFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.channels_list_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onSearch(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    abstract fun onSearch(query: String?)
}