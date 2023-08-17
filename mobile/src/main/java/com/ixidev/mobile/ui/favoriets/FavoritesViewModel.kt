package com.ixidev.mobile.ui.favoriets

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MovieItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _favorites: LiveData<PagingData<MovieItem>> by lazy {
        Pager(
            config = PagingConfig(10, 10, false),
            pagingSourceFactory = {
                repository.getAllFavorites()
            }
        ).liveData.cachedIn(viewModelScope)
    }

    fun getFavorites(): LiveData<PagingData<MovieItem>> {
        return _favorites
    }

    fun changeChannelItemFav(item: MovieItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMovieItemFav(item)
        }
    }

}