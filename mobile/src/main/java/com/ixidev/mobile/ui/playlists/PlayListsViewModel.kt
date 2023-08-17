package com.ixidev.mobile.ui.playlists

import android.util.Log
import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ixidev.data.MoviesRepository
import com.ixidev.data.model.MoviesPlayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListsViewModel @Inject constructor(
    private val repository: MoviesRepository
) : ViewModel() {


    private val selectedItems: ArraySet<Int> = ArraySet()
    private val _selectionMode: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _selectedItemsCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedItemsCount: LiveData<Int>
        get() = _selectedItemsCount
    val selectionMode: LiveData<Boolean>
        get() = _selectionMode

    private val _playLists: LiveData<PagingData<MoviesPlayList>> by lazy {
        val pager = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                repository.getAllPlayLists()
            }
        )
        pager.liveData.cachedIn(viewModelScope)
    }

    fun getAllPlayLists(): LiveData<PagingData<MoviesPlayList>> {
        return _playLists
    }

    suspend fun setCurrentPlayList(id: Int) {
        repository.setCurrentPlaylist(id)
    }

    fun isPlayListSelected(listId: Int): Boolean {
        return selectedItems.contains(listId)
    }

    fun onPlayListSelectionChange(id: Int, selected: Boolean) {
        if (selected)
            selectedItems.add(id)
        else selectedItems.remove(id)
        _selectedItemsCount.value = selectedItems.size
    }

    fun setSelectionModeEnabled(enabled: Boolean) {
        this._selectedItemsCount.value = 0
        this.selectedItems.clear()
        this._selectionMode.value = enabled
    }

    fun deleteSelectedItems(): Boolean {
        viewModelScope.launch {
            try {
                repository.deletePlaylistsByIds(selectedItems)
                setSelectionModeEnabled(false)
            } catch (e: Exception) {
                Log.e("PlayListsViewModel", "deleteSelectedItems: ", e)
            }
        }
        return true
    }

    fun deleteRefreshedItem(id: Int): Boolean {
        viewModelScope.launch {
            try {
                repository.deleteSinglePlaylistById(id)
            } catch (e: Exception) {
                Log.e("PlayListsViewModel", "deleteSelectedItems: ", e)
            }
        }
        return true
    }


}