package com.ixidev.data.di

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by ABDELMAJID ID ALI on 4/4/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@Module
@InstallIn(ViewModelComponent::class)
object VolleyModule {
    @Provides
    @ViewModelScoped
    fun provideVolley(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context)
    }
}