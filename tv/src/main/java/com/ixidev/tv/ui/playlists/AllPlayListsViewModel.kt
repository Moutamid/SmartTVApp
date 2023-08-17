package com.ixidev.tv.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MoviesPlayList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllPlayListsViewModel @Inject constructor(private val repository: MoviesRepository) :
    ViewModel() {

    private val _moviesPlayList: LiveData<PagingData<MoviesPlayList>> by lazy {
        val pager = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                repository.getAllPlayLists()
            }
        )
        pager.liveData.cachedIn(viewModelScope)
    }


    fun getMoviesList(): LiveData<PagingData<MoviesPlayList>> {
        return _moviesPlayList
    }
}