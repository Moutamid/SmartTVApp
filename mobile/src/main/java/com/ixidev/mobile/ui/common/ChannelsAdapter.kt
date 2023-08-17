package com.ixidev.mobile.ui.common

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.data.model.MovieItem
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ChannelItemLayoutBinding

typealias onMovieClickListener = (movie: MovieItem) -> Unit
typealias onMovieFavChange = (movie: MovieItem) -> Unit

open class ChannelsAdapter(
    private val movieClickListener: onMovieClickListener,
    private val movieFavChange: onMovieFavChange
) : PagingDataAdapter<MovieItem, RecyclerView.ViewHolder>(itemDiff) {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChannelViewHolder)
            getItem(position)?.let { movieItem ->
                holder.bind(movieItem)
                holder.itemView.setOnClickListener {
                    onChannelItemClick(position, it, movieItem)
                }
                holder.itemView.setOnLongClickListener { view ->
                    onChannelItemLongClick(view, movieItem, position)
                }
            }
    }

    @CallSuper
    open fun onChannelItemClick(position: Int, view: View, movieItem: MovieItem) {
        movieClickListener(movieItem)
    }

    private fun onChannelItemLongClick(
        view: View,
        movieItem: MovieItem,
        position: Int
    ): Boolean {
        PopupMenu(view.context, view, Gravity.TOP).apply {
            this.inflate(R.menu.channel_item_popup_menu)
            menu.findItem(R.id.action_add_channel_to_fav)
                .title = if (movieItem.favorite)
                view.context.getString(R.string.remove_from_fav)
            else view.context.getString(R.string.add_to_favorites)
            this.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_add_channel_to_fav) {
                    movieFavChange.invoke(movieItem)
                    notifyItemChanged(position)
                }
                false
            }
        }.show()
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChannelViewHolder(
            ChannelItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    companion object {
        private val itemDiff = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return if ((oldItem.id == -1) && (newItem.id == -1) && (newItem.listId == -1) && (oldItem.listId == -1))
                    false
                else
                    oldItem == newItem
            }
        }
    }
}