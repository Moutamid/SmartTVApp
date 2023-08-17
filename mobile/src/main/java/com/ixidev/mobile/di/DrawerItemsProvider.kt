package com.ixidev.mobile.di

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class DrawerItemsProvider(
    val items: List<IDrawerItem<*>>,
    val stickyItems: List<IDrawerItem<*>>
) {

    fun contains(item: IDrawerItem<*>): Boolean {
        return items.contains(item) or stickyItems.contains(item)
    }
}