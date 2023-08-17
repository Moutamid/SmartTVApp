package com.ixidev.data

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.ixidev.data.db.DbDao
import com.ixidev.data.model.*

/**
 * Created by ABDELMAJID ID ALI on 31/10/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class MoviesRepoImpl(private val dao: DbDao) : MoviesRepository {

    override fun getAllPlayLists(): PagingSource<Int, MoviesPlayList> {
        return dao.getPlayLists()
    }

    override fun getPlayListCategories(listId: Int): PagingSource<Int, CategoryItem> {
        return dao.getPlayListCategories(listId)
    }

    override fun getMoviesByCategory(listId: Int, category: String?): PagingSource<Int, MovieItem> {
        if (category == null || category == "All")
            return dao.getAllListMovies(listId)
        return dao.getCategoryMovies(listId, category)
    }

    override fun getAllFavorites(): PagingSource<Int, MovieItem> {
        return dao.getAllFavorites()
    }

    override  fun getMenuItems(): LiveData<List<MenuPlayListItem>> {
        return dao.getMenuItems()
    }

    override fun getChannels(filters: PlayListFilters?): PagingSource<Int, MovieItem> {
        if (filters?.category.isNullOrEmpty())
            return dao.getChannels(
                filters?.id ?: 0,
                "%${filters?.query ?: ""}%",
            )
        return dao.getChannels(
            filters?.id ?: 0,
            "%${filters?.category ?: ""}%",
            "%${filters?.query ?: ""}%",
        )
    }

    override fun getCategories(playListId: Int): LiveData<List<String>> {
        return dao.getCategoriesByListId(playListId)
    }


    override fun getCurrentPlayList(): LiveData<MoviesPlayList> {
        return dao.getCurrentPlayList()
    }

    override  suspend fun setCurrentPlaylist(playListId: Int) {
        dao.unSelectAllPlayLists()
        dao.setCurrentPlayList(playListId)
    }

    override suspend fun setMovieItemFav(item: MovieItem) {
        item.favorite = !item.favorite
        dao.updateMovieItem(item)
    }

   override suspend fun deletePlaylistsByIds(items: ArraySet<Int>) {
        for (id in items) {
            if (id != null) {
                dao.deletePlaylistsById(id)
                dao.deleteAllPlaylistChannels(id)
            }
        }
    }

    override suspend fun deleteSinglePlaylistById(id: Int) {
        dao.deletePlaylistsById(id)
        dao.deleteAllPlaylistChannels(id)
    }
}