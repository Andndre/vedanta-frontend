package com.example.brahmacarlearning.view.user.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.brahmacarlearning.data.remote.response.SessionsItem
import com.example.brahmacarlearning.databinding.ItemChatBySessionBinding
import com.example.brahmacarlearning.view.user.chat.ChatActivity

class DrawerAdapter(private val context: Context) : ListAdapter<SessionsItem, DrawerAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SessionsItem>() {
            override fun areItemsTheSame(oldItem: SessionsItem, newItem: SessionsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SessionsItem, newItem: SessionsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemChatBySessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drawerItem = getItem(position)
        holder.bind(drawerItem)
        holder.itemView.setOnClickListener {
            val moveDetailChat = Intent(holder.itemView.context, ChatActivity::class.java)
            moveDetailChat.putExtra(ChatActivity.ID, drawerItem.id)

            context.startActivity(moveDetailChat)
        }
    }

    class MyViewHolder(private val binding: ItemChatBySessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(drawerItem: SessionsItem) {
            with(binding) {
                textView.text = drawerItem.id
            }
        }
    }
}
