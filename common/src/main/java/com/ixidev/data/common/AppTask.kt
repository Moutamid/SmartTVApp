package com.ixidev.data.common

/**
 * Created by ABDELMAJID ID ALI on 04/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

sealed class AppTask<T> {
    data class Loading(val message: String = "Loading...") : AppTask<String>()
    data class Progressing(val progress: Int) : AppTask<Int>()
    data class Success<T>(val data: T) : AppTask<T>()
    data class Error(val error: Exception) : AppTask<Exception>()
}