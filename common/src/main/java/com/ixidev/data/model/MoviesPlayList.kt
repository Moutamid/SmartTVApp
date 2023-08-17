package com.ixidev.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

abstract class AbstractMoviesList : Serializable {
    abstract val id: Int
    abstract var name: String
    abstract var fileUrl: String
}

/**
 * Created by ABDELMAJID ID ALI on 02/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Entity(tableName = "playlist")
data class MoviesPlayList(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    override var name: String,
    override var fileUrl: String,
    var current: Boolean = false
) : AbstractMoviesList()