package com.ixidev.data.logger

import android.content.Context
import com.ixidev.data.BuildConfig
import com.ixidev.data.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Created by ABDELMAJID ID ALI on 3/9/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@InstallIn(SingletonComponent::class)
@Module
object LoggerModule {


    @Provides
    fun provideLogger(@ApplicationContext context: Context): IAppLogger {
        return AppLogger(
            context.getString(R.string.app_name),
            BuildConfig.DEBUG
        )
    }
}