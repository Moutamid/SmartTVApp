package com.ixidev.data.di

import android.content.Context
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.File


/**
 * Created by ABDELMAJID ID ALI on 04/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@InstallIn(FragmentComponent::class, ActivityComponent::class)
@Module
object UIModule {

    @Provides
    fun provideFileBrowser(@ActivityContext context: Context): FilePickerDialog {
        val properties = DialogProperties().apply {
            selection_mode = DialogConfigs.SINGLE_MODE
            selection_type = DialogConfigs.FILE_SELECT
            root = File(DialogConfigs.DEFAULT_DIR)
            error_dir = File(DialogConfigs.DEFAULT_DIR)
            //offset = new File (DialogConfigs.DEFAULT_DIR);
            extensions = arrayOf("m3u", "ts", "m3u8")
            show_hidden_files = false
        }
        return FilePickerDialog(context, properties).apply {
            setTitle("Select a File")
        }
    }
}