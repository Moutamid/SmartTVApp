package com.ixidev.data

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.ixidev.data.model.*

/**
 * Created by ABDELMAJID ID ALI on 31/10/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
interface MoviesRepository {

    fun getAllPlayLists(): PagingSource<Int, MoviesPlayList>

    fun getPlayListCategories(listId: Int): PagingSource<Int, CategoryItem>

    fun getMoviesByCategory(listId: Int, category: String?): PagingSource<Int, MovieItem>

    fun getAllFavorites(): PagingSource<Int, MovieItem>

    fun getMenuItems(): LiveData<List<MenuPlayListItem>>

    fun getChannels(filters: PlayListFilters?): PagingSource<Int, MovieItem>

    fun getCategories(playListId: Int): LiveData<List<String>>

    fun getCurrentPlayList(): LiveData<MoviesPlayList>

    suspend fun setCurrentPlaylist(playListId: Int)

    suspend fun setMovieItemFav(item: MovieItem)

    suspend fun deletePlaylistsByIds(items: ArraySet<Int>)

    suspend fun deleteSinglePlaylistById(id: Int)
}