package com.ixidev.smarttvaapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.ixidev.data.MoviesRepoImpl
import com.ixidev.data.MoviesRepository
import com.ixidev.data.db.DbDao
import com.ixidev.data.db.SmartTvDatabase
import com.ixidev.data.di.CacheDir
import com.ixidev.data.di.DataModule
import com.ixidev.data.di.VolleyModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.io.File
import javax.inject.Singleton

/**
 * Created by ABDELMAJID ID ALI on 3/13/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class, VolleyModule::class]
)

class FakeDataModule {
    @Provides
    @Singleton
    fun provideRepo(dao: DbDao): MoviesRepository {
        return MoviesRepoImpl(dao)
    }

//    @Provides
//    @Singleton
//    fun provideDb(@ApplicationContext context: Context): SmartTvDatabase {
//        return Room.databaseBuilder(
//            context, SmartTvDatabase::class.java, "app_db_test"
//        ).build()
//    }

    @Provides
    @Singleton
    fun provideDao(): DbDao {
        return FakeDbDao()
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

    @Provides
    fun provideVolley(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context)
    }
}