package com.mibaldi.fitapp.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.common.basicDiffUtil
import com.mibaldi.fitapp.ui.common.inflate
import kotlinx.android.synthetic.main.view_training.view.*

class TagsAdapter(private val listener: (Tag) -> Unit): RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    var tags: List<Tag> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = {old,new -> old.tag == new.tag }
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_tag,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = tags.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]
        holder.bind(tag)
        holder.itemView.setOnClickListener { listener(tag) }
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(tag: Tag){
            (itemView as Chip).text = tag.name
        }
    }
}