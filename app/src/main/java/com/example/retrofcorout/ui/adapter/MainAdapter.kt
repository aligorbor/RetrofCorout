package com.example.retrofcorout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.retrofcorout.R
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.ItemLayoutBinding

class MainAdapter(private val users: ArrayList<User>) :
    RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemLayoutBinding.bind(itemView)
        fun bind(user: User) {
            with(binding) {
                itemView.apply {
                    textViewUserName.text = user.name
                    textViewUserEmail.text = user.email
                    Glide.with(imageViewAvatar.context)
                        .load(user.avatar)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .into(imageViewAvatar)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(users[position])
    }

    fun addUsers(users: List<User>) {
        this.users.apply {
            clear()
            addAll(users)
        }
    }

}