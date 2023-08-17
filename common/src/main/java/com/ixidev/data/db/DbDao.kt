package com.ixidev.data.db

import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ixidev.data.model.CategoryItem
import com.ixidev.data.model.MenuPlayListItem
import com.ixidev.data.model.MovieItem
import com.ixidev.data.model.MoviesPlayList

/**
 * Created by ABDELMAJID ID ALI on 04/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Dao
interface DbDao {
    @Insert
    suspend fun insertPlayList(moviesPlayList: MoviesPlayList): Long

    @Insert
    suspend fun insertMovies(movies: List<MovieItem>): List<Long>

    @Query("SELECT * FROM playlist ORDER BY id DESC")
    fun getPlayLists(): PagingSource<Int, MoviesPlayList>

    // @Query("SELECT DISTINCT categorie as title , count( id) as count FROM movie WHERE listId=:listId and categorie not null group by categorie ")
    @Query("SELECT ifnull(categorie,'All') as title, count(1) as count FROM movie WHERE listId=:listId GROUP BY categorie ")
    fun getPlayListCategories(listId: Int): PagingSource<Int, CategoryItem>

    @Query("SELECT * FROM movie WHERE listId=:listId AND categorie like :category")
    fun getCategoryMovies(listId: Int, category: String?): PagingSource<Int, MovieItem>

    @Query("SELECT * FROM movie WHERE listId=:listId")
    fun getAllListMovies(listId: Int): PagingSource<Int, MovieItem>

    @Query("SELECT p.* , count(m.id) as 'count' FROM playlist p   JOIN movie m WHERE m.listId=p.id GROUP BY p.id ORDER BY id DESC")
    fun getMenuItems(): LiveData<List<MenuPlayListItem>>

    @Query("UPDATE playlist SET current=0")
    suspend fun unSelectAllPlayLists()

    @Query("UPDATE playlist SET current=1 WHERE id=:id")
    suspend fun setCurrentPlayList(id: Int)

    @Query("UPDATE movie SET favorite=:fav WHERE id=:id")
    suspend fun setMovieItemFav(id: Int, fav: Boolean)

    @Query("SELECT * FROM movie WHERE favorite=1")
    fun getAllFavorites(): PagingSource<Int, MovieItem>

    @Query("SELECT DISTINCT categorie FROM movie WHERE listId=:id AND categorie not null")
    fun getCategoriesByListId(id: Int): LiveData<List<String>>

    @Query("SELECT * FROM playlist WHERE current=1 LIMIT 1")
    fun getCurrentPlayList(): LiveData<MoviesPlayList>


    @Query("SELECT * FROM movie WHERE listId=:id AND categorie LIKE :cat  AND title LIKE :q")
    fun getChannels(id: Int, cat: String, q: String): PagingSource<Int, MovieItem>

    @Query("SELECT * FROM movie WHERE listId=:id  AND title LIKE :q")
    fun getChannels(id: Int, q: String): PagingSource<Int, MovieItem>

    @Update
    suspend fun updateMovieItem(item: MovieItem)

    @Query("DELETE FROM playlist WHERE id=:playListId")
    suspend fun removePlaylist(playListId: Int)

    @Query("DELETE FROM playlist WHERE id =:playListId")
    suspend fun deletePlaylistsById(playListId: Int)

    @Query("DELETE FROM movie WHERE listId =:id")
    suspend fun deleteAllPlaylistChannels(id: Int)

}