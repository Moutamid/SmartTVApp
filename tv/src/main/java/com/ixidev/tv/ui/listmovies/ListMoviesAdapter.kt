package com.ixidev.tv.ui.listmovies

import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ixidev.data.model.MovieItem
import com.ixidev.tv.R
import com.ixidev.tv.databinding.MovieItemLayoutBinding

/**
 * Created by ABDELMAJID ID ALI on 06/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Suppress("unused")
private const val TAG = "ListMoviesAdapter"
typealias onMovieClickListener = (movie: MovieItem) -> Unit

class ListMoviesAdapter(
    private val movieClickListener: onMovieClickListener
) :
    PagingDataAdapter<MovieItem, ListMoviesAdapter.MovieHolder>(itemDiff) {


    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        getItem(position)?.let { movieItem ->
            holder.bind(movieItem)
            holder.itemView.setOnClickListener {
                movieClickListener(movieItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        return MovieHolder(
            MovieItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    companion object {
        private val itemDiff = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MovieHolder(private val view: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun bind(item: MovieItem) {
            if (Patterns.WEB_URL.matcher(item.thumbnail ?: "").matches()) {
                view.movieThumbnail.load(item.thumbnail) {
                    error(R.drawable.ic_thumbnail_image)
                    placeholder(R.drawable.ic_thumbnail_image)
                }
            } else {
                view.movieThumbnail.load(R.drawable.ic_thumbnail_image)
            }
            view.movieTitle.text = item.title
        }
    }


}