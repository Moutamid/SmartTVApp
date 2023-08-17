package com.ixidev.mobile.ui.playlists

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.ixidev.mobile.R

/**
 * Created by ABDELMAJID ID ALI on 2/25/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class PlayListSelectionCallBack(
    private val deleteItems: () -> Unit
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.playlists_action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete_items) {
            deleteItems.invoke()
            return true
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {

    }
}