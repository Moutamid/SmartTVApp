package com.ixidev.tv.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.data.model.CategoryItem
import com.ixidev.tv.databinding.CategoryItemLayoutBinding

/**
 * Created by ABDELMAJID ID ALI on 05/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
typealias OnCategoryClickListener = (category: String?) -> Unit

class CategoriesAdapter(private val categoryClickListener: OnCategoryClickListener) :
    PagingDataAdapter<CategoryItem, CategoriesAdapter.CategoryHolder>(itemDiff) {


    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
            holder.itemView.setOnClickListener {
                categoryClickListener(item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = CategoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return CategoryHolder(binding)
    }

    companion object {
        private val itemDiff = object : DiffUtil.ItemCallback<CategoryItem>() {
            override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    class CategoryHolder(private val view: CategoryItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(item: CategoryItem) {
            view.categoryName.text = item.title
            view.categoryName.isSelected = true
            view.categoryCount.text = "${item.count}"
        }
    }
}