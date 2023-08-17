package com.ixidev.mobile.ui.player

import android.graphics.Color
import android.util.Patterns
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ixidev.data.model.MovieItem
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ChannelItemLayoutBinding
import com.ixidev.mobile.databinding.PlayerMenuChannelItemBinding
import com.ixidev.mobile.ui.common.ChannelViewHolder
import com.ixidev.mobile.ui.common.ChannelsAdapter
import com.ixidev.mobile.ui.common.onMovieClickListener
import com.ixidev.mobile.ui.common.onMovieFavChange

/**
 * Created by ABDELMAJID ID ALI on 2/10/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class PlayerChannelsAdapter(
    movieClickListener: onMovieClickListener,
    movieFavChange: onMovieFavChange,
) : ChannelsAdapter(movieClickListener, movieFavChange) {
    private var currentUrl: String? = null
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rootView = inflater.inflate(R.layout.player_menu_channel_item, parent, false)
        val binding = ChannelItemLayoutBinding.bind(rootView)
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (isItemSelected(position)) {
            selectedPosition = position
            holder.itemView.setBackgroundColor(Color.parseColor("#6371c7"))
            holder.itemView.findViewById<TextView?>(R.id.channel_item_name).setTextColor(Color.parseColor("#FFFFFF"))
        } else
            holder.itemView.findViewById<TextView?>(R.id.channel_item_name).setTextColor(Color.parseColor("#FFFFFF"))
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun isItemSelected(position: Int): Boolean {
        return getItem(position)?.sourceUrl == currentUrl
    }

    override fun onChannelItemClick(position: Int, view: View, movieItem: MovieItem) {
        super.onChannelItemClick(position, view, movieItem)
        this.currentUrl = movieItem.sourceUrl
        notifyItemChanged(selectedPosition)
        selectedPosition = position
        notifyItemChanged(position)
    }

    fun setSelectedUrl(currentUrl: String) {
        this.currentUrl = currentUrl
        notifyDataSetChanged()
    }
}

