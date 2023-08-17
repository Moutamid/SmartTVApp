package com.ixidev.mobile.ui.playlists

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.data.model.MoviesPlayList
import com.ixidev.mobile.databinding.MobilePlaylistItemLayoutBinding

typealias onPlayListClick = (playlistId: Int) -> Unit
typealias onPlayListSelectedChange = (playlistId: Int, selected: Boolean) -> Unit
typealias onEnableSelectionMode = () -> Unit
typealias isItemSelected = (playlistId: Int) -> Boolean
typealias onItemRefreshClick = (moviesPlayList: MoviesPlayList) -> Unit

class MobilePlayListsAdapter(
    private val onPlayListClick: onPlayListClick,
    private val onPlayListSelectedChange: onPlayListSelectedChange,
    private val onEnableSelectionMode: onEnableSelectionMode,
    private val isItemSelected: isItemSelected,
    private val onItemRefreshClick: onItemRefreshClick
) : PagingDataAdapter<MoviesPlayList, MobilePlayListsAdapter.PlayListHolder>(playListDiff) {

    private var selectionMode: Boolean = false
    override fun onBindViewHolder(holder: PlayListHolder, position: Int) {
        getItem(position)?.let { playListItem ->
            holder.bind(playListItem, selectionMode, onPlayListSelectedChange, onItemRefreshClick)
            holder.setSelected(isItemSelected.invoke(playListItem.id))
            holder.itemView.setOnClickListener {
                if (selectionMode)
                    holder.performCheckClick()
                else
                    onPlayListClick(playListItem.id)
            }
            holder.itemView.setOnLongClickListener {
                if (selectionMode)
                    false
                else {
                    onEnableSelectionMode.invoke()
                    holder.performCheckClick()
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListHolder {
        val binding = MobilePlaylistItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayListHolder(binding)
    }

    fun enableSelectionMode(isSelectionMode: Boolean) {
        this.selectionMode = isSelectionMode
        notifyDataSetChanged()
    }


    class PlayListHolder(private val bindingView: MobilePlaylistItemLayoutBinding) :
        RecyclerView.ViewHolder(bindingView.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(
            moviesPlayList: MoviesPlayList,
            selectionMode: Boolean,
            onPlayListSelectedChange: onPlayListSelectedChange,
            onItemRefreshClick: onItemRefreshClick
        ) {
            bindingView.playlistName.text = moviesPlayList.name
            bindingView.playListUrl.text = moviesPlayList.fileUrl
            bindingView.playListUrl.isSelected = true
            if (moviesPlayList.current)
                bindingView.root.setCardBackgroundColor("#E6E8F5".toColorInt())
            else {
                bindingView.root.setCardBackgroundColor(
                    getCardBackColor(bindingView.root.context)
                )
            }
            bindingView.playlistCheckBox.isVisible = selectionMode
            bindingView.playlistCheckBox.setOnClickListener {
                val isSelected = bindingView.playlistCheckBox.isChecked
                onPlayListSelectedChange.invoke(
                    moviesPlayList.id,
                    isSelected
                )
            }
            bindingView.refreshBtn.setOnClickListener {
                onItemRefreshClick.invoke(moviesPlayList)
            }
        }

        fun performCheckClick() {
            bindingView.playlistCheckBox.performClick()
        }

        fun setSelected(itemSelected: Boolean) {
            bindingView.playlistCheckBox.isChecked = itemSelected
        }
    }


    companion object {
        private val playListDiff = object : DiffUtil.ItemCallback<MoviesPlayList>() {
            override fun areItemsTheSame(
                oldItem: MoviesPlayList,
                newItem: MoviesPlayList
            ): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(
                oldItem: MoviesPlayList,
                newItem: MoviesPlayList
            ): Boolean {
                return oldItem == newItem
            }

        }

        fun getCardBackColor(context: Context): ColorStateList {
            val aa: TypedArray =
                context.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground))
            val themeColorBackground = aa.getColor(0, 0)
            aa.recycle()
            val hsv = FloatArray(3)
            Color.colorToHSV(themeColorBackground, hsv)
            @Suppress("DEPRECATION")
            return ColorStateList.valueOf(
                if (hsv[2] > 0.5f)
                    context.resources.getColor(
                        androidx.cardview.R.color.cardview_light_background
                    )
                else context.resources.getColor(
                    androidx.cardview.R.color.cardview_dark_background
                )
            )
        }
    }
}