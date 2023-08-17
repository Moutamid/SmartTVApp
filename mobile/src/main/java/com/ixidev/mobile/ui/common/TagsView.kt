package com.ixidev.mobile.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.mobile.R

/**
 * Created by ABDELMAJID ID ALI on 18/09/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
class TagsView : FrameLayout {

    private var recyclerView: RecyclerView? = null
    private var adapter: Adapter? = null

    constructor(context: Context) : super(context) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(context)
    }

    private fun initViews(context: Context) {
        recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        addView(recyclerView)
    }

    fun setAdapter(tagsAdapter: Adapter) {
        this.adapter = tagsAdapter
        recyclerView?.adapter = adapter
    }

    fun setTags(tags: List<String>) {
        if (adapter == null)
            error("Must call setAdapter() first")
        adapter?.tags?.clear()
        adapter?.tags?.addAll(tags)
        adapter?.notifyDataSetChanged()
    }

    fun clear() {
        adapter?.tags?.clear()
        adapter?.notifyDataSetChanged()
    }

}

/*
interface OnTagSelectedListener {
    fun onTagSelected(tag: String)
}
*/

class Adapter(
    val tags: ArrayList<String?> = ArrayList(),
    private val tagLayoutId: Int = -1,
    private val tagTextViewId: Int = -1,
    private var onTagSelected: (tag: String, position: Int) -> Unit = { _: String, _: Int -> }
) : RecyclerView.Adapter<Adapter.TagHolder>() {

    private var selectedTag = 0

    @Suppress("unused")
    private val selectedTagBackgroundColor = Color.parseColor("#4CAF50")
    private val tagBackgroundColor = Color.parseColor("#1565C0")
    private val params = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(4, 0, 4, 0)

    }

    class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(layoutId: Int, textViewId: Int, tag: String) {
            if (layoutId != -1) {
                if (textViewId == -1)
                    error("You mast set tag textView id textViewId=$textViewId")
                itemView.findViewById<TextView>(textViewId).text = tag
//                (itemView as TextView).setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                (itemView as TextView).text = tag
            }
        }

        fun setSelected(selected: Boolean) {
            itemView.isSelected = selected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        if (tagLayoutId == -1) {
            return TagHolder(TextView(parent.context).apply {
                setTextColor(Color.WHITE)
                setBackgroundColor(tagBackgroundColor)
                layoutParams = params
                gravity = Gravity.CENTER
            })
        }
        if (tagTextViewId == -1) {
            error("You mast set tag textView id tagTextViewId=$tagTextViewId")
        }
        return TagHolder(
            LayoutInflater.from(parent.context).inflate(tagLayoutId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        tags[position]?.let { tag ->
            holder.bind(tagLayoutId, tagTextViewId, tag)
            holder.itemView.setOnClickListener {
                val old = selectedTag
                selectedTag = position
                notifyItemChanged(old)
                notifyItemChanged(position)
                onTagSelected(tag, position)
            }
            val selected = selectedTag == position
            holder.setSelected(selected)
//            (holder.itemView as? CardView)?.let {
//                if (selected) {
//                    it.inverseBackgroundColor()
//                } else it.resetBackgroundColor()
//            }
        }
    }

    override fun getItemCount(): Int = tags.size
}

@Suppress("unused")
private fun CardView.resetBackgroundColor() {
    val aa: TypedArray =
        context.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground))
    val themeColorBackground = aa.getColor(0, 0)
    aa.recycle()
    val hsv = FloatArray(3)
    Color.colorToHSV(themeColorBackground, hsv)
    @Suppress("DEPRECATION")
    setCardBackgroundColor(
        ColorStateList.valueOf(
            if (hsv[2] > 0.5f)
                context.resources.getColor(
                    androidx.cardview.R.color.cardview_light_background
                )
            else context.resources.getColor(
                androidx.cardview.R.color.cardview_dark_background
            )
        )
    )
}

@Suppress("unused")
private fun CardView.inverseBackgroundColor() {
    val aa: TypedArray =
        context.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground))
    val themeColorBackground = aa.getColor(0, 0)
    aa.recycle()
    val hsv = FloatArray(3)
    Color.colorToHSV(themeColorBackground, hsv)
    @Suppress("DEPRECATION")
    setCardBackgroundColor(
        ColorStateList.valueOf(
            if (hsv[2] > 0.5f)
                context.resources.getColor(
                    androidx.cardview.R.color.cardview_dark_background
                )
            else context.resources.getColor(
                androidx.cardview.R.color.cardview_light_background
            )
        )
    )
}