package com.ixidev.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ixidev.data.model.MovieItem
import com.ixidev.data.model.MoviesPlayList

/**
 * Created by ABDELMAJID ID ALI on 04/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@Database(
    entities = [MovieItem::class, MoviesPlayList::class],
    version = 1,
    exportSchema = false
)
abstract class SmartTvDatabase : RoomDatabase() {
    abstract fun dao(): DbDao
}