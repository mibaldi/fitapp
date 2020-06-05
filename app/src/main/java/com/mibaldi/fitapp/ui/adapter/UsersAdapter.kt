package com.mibaldi.fitapp.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mibaldi.domain.Training
import com.mibaldi.domain.User
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.common.basicDiffUtil
import com.mibaldi.fitapp.ui.common.inflate
import kotlinx.android.synthetic.main.view_training.view.*
import kotlinx.android.synthetic.main.view_user.view.*

class UsersAdapter(private val listener: (User) -> Unit): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    var users: List<User> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = {old,new -> old.id == new.id }
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_user,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener { listener(user) }
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User){
            itemView.userName.text =  user.email
        }
    }
}