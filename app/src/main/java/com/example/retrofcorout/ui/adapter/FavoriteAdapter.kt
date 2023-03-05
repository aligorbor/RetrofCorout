package com.example.retrofcorout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofcorout.R
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.ItemLayoutBinding


    class FavoriteAdapter(private val fragment: Fragment) :
        ListAdapter<User, FavoriteAdapter.DataViewHolder>(USERS_COMPARATOR) {

    var clickListenerToEdit = MutableLiveData<User>()
    var menuPosition: Int = 0
    var menuUser: User? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemLayoutBinding.bind(itemView)
        var userInViewHolder : User? = null
        fun bind(user: User) {
            val position = bindingAdapterPosition
            userInViewHolder = user
            with(binding) {
                varUser = user
                itemView.apply {
                    root.setOnClickListener {
                        clickListenerToEdit.postValue(user)
                    }
                    root.setOnLongClickListener {
                        menuPosition = position
                        menuUser = user
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

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val USERS_COMPARATOR = object : DiffUtil.ItemCallback<User>(){
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
               return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
               return oldItem.avatar == newItem.avatar &&
                      oldItem.name == newItem.name &&
                      oldItem.email == newItem.email &&
                      oldItem.dateBirth == newItem.dateBirth
            }

        }
    }

}