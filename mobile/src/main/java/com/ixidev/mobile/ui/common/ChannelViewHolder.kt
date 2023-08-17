package com.ixidev.mobile.ui.common

import android.util.Patterns
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ixidev.data.model.MovieItem
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ChannelItemLayoutBinding

class ChannelViewHolder(
    private val bindingView: ChannelItemLayoutBinding
) : RecyclerView.ViewHolder(bindingView.root) {

    fun bind(movie: MovieItem) {
        bindingView.channelItemName.text = movie.title?.parseAsHtml()
        if (Patterns.WEB_URL.matcher(movie.thumbnail ?: "").matches()) {
            bindingView.channelItemImage.load(movie.thumbnail) {
                placeholder(R.drawable.ic_thumbnail_loading)
                error(R.drawable.ic_thumbnail_error)
                transformations(CircleCropTransformation())
            }
        } else {
            bindingView.channelItemImage.load(R.drawable.ic_thumbnail_error)
        }

        if (movie.favorite) {
            bindingView.channelItemFav.visibility = View.VISIBLE
        } else {
            bindingView.channelItemFav.visibility = View.INVISIBLE
        }

//        bindingView.channelItemFav.isVisible = movie.favorite
    }
}