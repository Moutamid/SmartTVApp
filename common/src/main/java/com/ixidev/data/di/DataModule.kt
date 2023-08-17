package com.ixidev.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.ixidev.data.MoviesRepoImpl
import com.ixidev.data.MoviesRepository
import com.ixidev.data.db.DbDao
import com.ixidev.data.db.SmartTvDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

/**
 * Created by ABDELMAJID ID ALI on 31/10/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideRepo(dao: DbDao): MoviesRepository {
        return MoviesRepoImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): SmartTvDatabase {
        return Room.databaseBuilder(
            context, SmartTvDatabase::class.java, "app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(smartTvDatabase: SmartTvDatabase): DbDao {
        return smartTvDatabase.dao()
    }

    @Provides
    @CacheDir
    fun provideCacheDir(@ApplicationContext context: Context): File {
        return context.cacheDir
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }
}