package com.ixidev.data

import com.ixidev.data.common.M3UFileParser
import com.ixidev.data.db.DbDao
import com.ixidev.data.model.MoviesPlayList
import java.io.File
import javax.inject.Inject

class FileParserRepository @Inject constructor(
    private val dao: DbDao
) {

    suspend fun parseFile(
        playPlayList: MoviesPlayList,
        path: String,
        @Suppress("UNUSED_PARAMETER") progress: (p: Int) -> Unit = {}
    ): Int {
        val playListId = dao.insertPlayList(playPlayList)
        val file = File(path)
        if (!file.exists() && !file.canRead()) {
            dao.removePlaylist(playListId.toInt())
            error("Cannot read this file or file not exist")
        }
        val movies = M3UFileParser.parseMovies(playListId.toInt(), file)
        dao.unSelectAllPlayLists()
        dao.setCurrentPlayList(playListId.toInt())
        return dao.insertMovies(movies).size
    }

}

