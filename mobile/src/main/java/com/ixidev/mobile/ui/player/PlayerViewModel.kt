package com.ixidev.mobile.ui.player

import androidx.lifecycle.*
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MovieItem
import com.ixidev.data.model.PlayListFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 2/10/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val query: MutableLiveData<String?> = MutableLiveData(null)

    val channels: LiveData<PagingData<MovieItem>> by lazy {
        query.switchMap { newQuery ->
            Pager(
                config = PagingConfig(10, 10, enablePlaceholders = false),
                pagingSourceFactory = {
                    repository.getChannels(
                        PlayListFilters(
                            id = stateHandle["listId"],
                            category = stateHandle["category"],
                            query = newQuery ?: ""
                        )
                    )
                }
            ).liveData.cachedIn(viewModelScope)
        }
    }

    fun changeChannelItemFav(item: MovieItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMovieItemFav(item)
        }
    }

    fun onSearch(queryText: String?) {
        this.query.value = queryText
    }
}