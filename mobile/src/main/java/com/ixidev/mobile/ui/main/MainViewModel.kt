package com.ixidev.mobile.ui.main

import androidx.lifecycle.*
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MenuPlayListItem
import com.ixidev.data.model.MovieItem
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.data.model.PlayListFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MoviesRepository
) : ViewModel() {

    // this for menu drawer
    val menuDrawerItems: LiveData<List<MenuPlayListItem>> by lazy {
        repository.getMenuItems()
    }
    private val filters: MutableLiveData<PlayListFilters?> by lazy {
        MutableLiveData(PlayListFilters(0, ""))
    }

    val playListChannels: LiveData<PagingData<MovieItem>> by lazy {
        filters.switchMap { filter ->
            Pager(
                config = PagingConfig(10, 10, enablePlaceholders = false),
                pagingSourceFactory = {
                    repository.getChannels(filter)
                }
            ).liveData.cachedIn(viewModelScope)
        }
    }

    val categories: LiveData<List<String>> by lazy {
        filters.switchMap { filter ->
            repository.getCategories(filter?.id ?: 0)
        }
    }

    val currentPlayList: LiveData<MoviesPlayList> by lazy {
        repository.getCurrentPlayList()
    }

    fun setSelectedPlayList(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(350)
            if (filters.value?.id != id)
                repository.setCurrentPlaylist(id)
        }
    }

    fun getSelectedPlayListId(): Long {
        return filters.value?.id?.toLong() ?: -1
    }

    fun changeChannelItemFav(item: MovieItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMovieItemFav(item)
        }
    }

    fun setSelectedCategory(cat: String?) {
        if (filters.value?.category != cat)
            filters.value = filters.value?.copy(
                category = cat
            )
    }

    fun setFilterByPlayList(currentPlayListId: Int) {
        if (filters.value?.id != currentPlayListId)
            filters.value = filters.value?.copy(
                id = currentPlayListId,
                category = ""
            )
    }

    fun onSearch(query: String?) {
        filters.value = filters.value?.copy(
            query = query
        )
    }


    companion object {
        @Suppress("unused")
        private const val TAG = "MainViewModel"
    }


}