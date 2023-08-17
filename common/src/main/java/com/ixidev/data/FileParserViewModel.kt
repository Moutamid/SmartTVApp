package com.ixidev.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.RequestQueue
import com.android.volley.request.DownloadRequest
import com.ixidev.data.common.AppTask
import com.ixidev.data.common.isUrl
import com.ixidev.data.di.CacheDir
import com.ixidev.data.model.MoviesPlayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

/**
 * Created by ABDELMAJID ID ALI on 4/4/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@HiltViewModel
class FileParserViewModel @Inject constructor(
    private val repository: FileParserRepository,
    @CacheDir private val cacheDir: File,
    private val volley: RequestQueue
) : ViewModel() {

    private val _parsFilesTask: MutableLiveData<AppTask<*>> = MutableLiveData(AppTask.Loading())
    val parsFilesTask: LiveData<AppTask<*>>
        get() = _parsFilesTask

    fun parsePlayList(playlist: MoviesPlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val count = if (playlist.fileUrl.isUrl()) {
                    loadM3uUrl(playlist)
                } else {
                    loadM3uFile(playlist, playlist.fileUrl)
                }
                _parsFilesTask.postValue(AppTask.Success(count))
            } catch (e: FileNotFoundException) {
                _parsFilesTask.value = AppTask.Error(
                    Exception(
                        "Error while lading data from url please try again"
                    )
                )
            } catch (e: Exception) {
                _parsFilesTask.postValue(AppTask.Error(e))
            }

        }
    }

    private suspend fun loadM3uFile(playlist: MoviesPlayList, path: String): Int {
        // val path = File(cacheDir, playlist.name).path
        _parsFilesTask.postValue(AppTask.Loading("Parsing file ...."))
        return repository.parseFile(playlist, path)

    }

    private suspend fun loadM3uUrl(playlist: MoviesPlayList): Int {
        _parsFilesTask.postValue(AppTask.Loading("Downloading file ...."))
        val path = File(cacheDir, playlist.name).path
        val loadedFile = download(playlist.fileUrl, path)
        return loadM3uFile(playlist.copy(), loadedFile)

    }

    private suspend fun download(link: String, path: String) = suspendCoroutine<String> { cor ->
        val downloadRequest = DownloadRequest(link, path,
            {
                // response listener
                cor.resumeWith(Result.success(it))
            },
            {
                // error
                cor.resumeWith(Result.failure(it))
            }
        )
        downloadRequest.setOnProgressListener { transferredBytes, totalSize ->
            // show progress
            val process = (transferredBytes * 100) / totalSize
            _parsFilesTask.postValue(AppTask.Progressing(process.toInt()))
        }
        volley.add(downloadRequest)
    }
}