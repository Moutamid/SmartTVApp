package com.ixidev.smarttvaapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.ixidev.data.FileParserRepository
import com.ixidev.data.FileParserViewModel
import com.ixidev.data.common.AppTask
import com.ixidev.data.db.DbDao
import com.ixidev.data.di.CacheDir
import com.ixidev.data.model.MoviesPlayList
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by ABDELMAJID ID ALI on 3/13/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@HiltAndroidTest
class MainActivityTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repo: FileParserRepository

    @Inject
    @CacheDir
    lateinit var cacheDir: File

    @Inject
    lateinit var volley: RequestQueue
    lateinit var viwModel: FileParserViewModel
    lateinit var dao: DbDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test1() {


        viwModel = FileParserViewModel(repo, cacheDir, volley)
        val playList = MoviesPlayList(
            0,
            "test",
            "https://iptv-org.github.io/iptv/index.m3u"
        )
    }


    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}