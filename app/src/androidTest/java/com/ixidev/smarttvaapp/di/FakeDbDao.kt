package com.ixidev.smarttvaapp.di

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.ixidev.data.db.DbDao
import com.ixidev.data.model.CategoryItem
import com.ixidev.data.model.MenuPlayListItem
import com.ixidev.data.model.MovieItem
import com.ixidev.data.model.MoviesPlayList

/**
 * Created by ABDELMAJID ID ALI on 3/14/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class FakeDbDao : DbDao {

    private val playlists: ArrayList<MoviesPlayList> by lazy {
        arrayListOf()
    }
    private val moviesItems: ArrayList<MovieItem> by lazy {
        arrayListOf()
    }

    override suspend fun insertPlayList(moviesPlayList: MoviesPlayList): Long {
        playlists.add(moviesPlayList)
        return playlists.indexOf(moviesPlayList).toLong()
    }

    override suspend fun insertMovies(movies: List<MovieItem>): List<Long> {
        val ids: ArrayList<Long> = arrayListOf()
        movies.forEach {
            this.moviesItems.add(it)
            ids.add(this.moviesItems.indexOf(it).toLong())
        }
        return ids
    }

    override fun getPlayLists(): PagingSource<Int, MoviesPlayList> {
        TODO("Not yet implemented")
    }

    override fun getPlayListCategories(listId: Int): PagingSource<Int, CategoryItem> {
        TODO("Not yet implemented")
    }

    override fun getCategoryMovies(listId: Int, category: String?): PagingSource<Int, MovieItem> {
        TODO("Not yet implemented")
    }

    override fun getAllListMovies(listId: Int): PagingSource<Int, MovieItem> {
        TODO("Not yet implemented")
    }

    override fun getMenuItems(): LiveData<List<MenuPlayListItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun unSelectAllPlayLists() {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentPlayList(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun setMovieItemFav(id: Int, fav: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAllFavorites(): PagingSource<Int, MovieItem> {
        TODO("Not yet implemented")
    }

    override fun getCategoriesByListId(id: Int): LiveData<List<String>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentPlayList(): LiveData<MoviesPlayList> {
        TODO("Not yet implemented")
    }

    override fun getChannels(id: Int, cat: String, q: String): PagingSource<Int, MovieItem> {
        TODO("Not yet implemented")
    }

    override fun getChannels(id: Int, q: String): PagingSource<Int, MovieItem> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMovieItem(item: MovieItem) {
        TODO("Not yet implemented")
    }

    override suspend fun removePlaylist(playListId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylistsById(playListId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPlaylistChannels(id: Int) {
        TODO("Not yet implemented")
    }
}