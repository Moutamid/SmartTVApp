package com.ixidev.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ABDELMAJID ID ALI on 02/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Entity(tableName = "movie")
data class MovieItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String? = null,
    var thumbnail: String? = null,
    var sourceUrl: String? = null,
    var categorie: String? = null,
    var listId: Int? = null,
    var favorite: Boolean = false
)