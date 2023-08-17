package com.ixidev.tv.ui.listmovies

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MovieItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 06/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@HiltViewModel
class ListMoviesViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _movies: LiveData<PagingData<MovieItem>> by lazy {
        val pager = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                repository.getMoviesByCategory(
                    state["listId"]!!,
                    state["category"]
                )
            }
        )
        pager.liveData.cachedIn(viewModelScope)
    }

    fun getCategoryMovies(): LiveData<PagingData<MovieItem>> {
        return _movies
    }


}