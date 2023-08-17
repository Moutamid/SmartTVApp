package com.ixidev.tv.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.tv.databinding.MovieListItemLayoutBinding

/**
 * Created by ABDELMAJID ID ALI on 05/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
typealias onPlayListClick = (playlistId: Int) -> Unit

class PlayListsAdapter(private val onPlayListClick: onPlayListClick) :
    PagingDataAdapter<MoviesPlayList, PlayListsAdapter.PlayListHolder>(
        playListDiff
    ) {


    override fun onBindViewHolder(holder: PlayListHolder, position: Int) {
        getItem(position)?.let { list ->
            holder.bind(list)
            holder.itemView.setOnClickListener {
                onPlayListClick(list.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListHolder {
        val binding = MovieListItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return PlayListHolder(binding)
    }


    class PlayListHolder(private val bindingView: MovieListItemLayoutBinding) :
        RecyclerView.ViewHolder(bindingView.root) {
        fun bind(moviesPlayList: MoviesPlayList) {
            bindingView.playListName.text = moviesPlayList.name
            bindingView.playListUrl.text = moviesPlayList.fileUrl
            bindingView.playListUrl.isSelected = true
        }
    }


    companion object {
        private val playListDiff = object : DiffUtil.ItemCallback<MoviesPlayList>() {
            override fun areItemsTheSame(oldItem: MoviesPlayList, newItem: MoviesPlayList): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: MoviesPlayList, newItem: MoviesPlayList): Boolean {
                return oldItem == newItem
            }

        }
    }
}