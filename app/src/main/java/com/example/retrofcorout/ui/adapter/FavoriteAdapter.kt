package com.example.retrofcorout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.retrofcorout.R
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.ItemLayoutBinding

class FavoriteAdapter(private val users: ArrayList<User>, private val fragment: Fragment) :
    RecyclerView.Adapter<FavoriteAdapter.DataViewHolder>() {

    var clickListenerToEdit = MutableLiveData<User>()
    var menuPosition: Int = 0
    var menuUser: User? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemLayoutBinding.bind(itemView)
        fun bind(user: User) {
            val position = bindingAdapterPosition
            with(binding) {
                itemView.apply {
                    textViewUserName.text = user.name
                    textViewUserEmail.text = user.email
                    Glide.with(imageViewAvatar.context)
                        .load(user.avatar)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .into(imageViewAvatar)
                    //       root.setOnLongClickListener {
                    root.setOnClickListener {
                        clickListenerToEdit.postValue(user)
                    }
                    root.setOnLongClickListener {
                        menuPosition = position
                        menuUser=user
                        false
                    }
                    fragment.registerForContextMenu(root)
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

    fun deleteUser(position: Int) =
        this.users.removeAt(position).id


}