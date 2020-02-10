package com.mibaldi.fitapp.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.common.basicDiffUtil
import com.mibaldi.fitapp.ui.common.inflate

class TrainingsAdapter(private val listener: (Training) -> Unit): RecyclerView.Adapter<TrainingsAdapter.ViewHolder>() {

    var trainings: List<Training> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = {old,new -> old.id == new.id }
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_training,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = trainings.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val training = trainings[position]
        holder.bind(training)
        holder.itemView.setOnClickListener { listener(training) }
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(training: Training){
        }
    }
}