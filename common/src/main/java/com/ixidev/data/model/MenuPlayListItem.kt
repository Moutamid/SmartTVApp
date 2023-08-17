package com.ixidev.data.model

import androidx.room.Embedded

data class MenuPlayListItem(
    @Embedded
    val playlist: MoviesPlayList,
    val count: Int = 0
)