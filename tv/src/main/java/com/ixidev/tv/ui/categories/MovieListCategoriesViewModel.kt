package com.ixidev.tv.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.CategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 05/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@HiltViewModel
class MovieListCategoriesViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _categories: LiveData<PagingData<CategoryItem>> by lazy {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = {
                repository.getPlayListCategories(state.get<Int>("listId")!!)
            }
        )
        pager.liveData.cachedIn(viewModelScope)
    }

    fun getMovieListCategories(): LiveData<PagingData<CategoryItem>> {
        return _categories
    }


}