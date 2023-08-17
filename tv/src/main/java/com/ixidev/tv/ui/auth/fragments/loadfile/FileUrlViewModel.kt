package com.ixidev.tv.ui.auth.fragments.loadfile

import android.util.Patterns
import androidx.lifecycle.ViewModel

@Suppress("unused")
private const val TAG = "FileUrlViewModel"

class FileUrlViewModel : ViewModel() {

    fun isM3uUrlValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun isXtreamUrlValid(url: String): Boolean {
        return url.matches(Patterns.WEB_URL.toRegex())
    }

}