package com.example.brahmacarlearning.view.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.brahmacarlearning.data.local.pref.ChatModel
import com.example.brahmacarlearning.databinding.ItemChatBinding

class ChatAdapter(private val context: Context) : ListAdapter<ChatModel, ChatAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem.date == newItem.date
            }
            override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
    }

    class MyViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatModel) {
            with(binding) {
                if (chat.sender == "Me") {
                    // Tampilkan pesan pengirim dan sembunyikan pesan penerima
                    senderName.text = chat.sender
                    senderMessage.text = chat.message
                    senderDate.text = chat.date
                    // Set visibilitas
                    senderName.visibility = View.VISIBLE
                    senderMessage.visibility = View.VISIBLE
                    senderDate.visibility = View.VISIBLE

                    receiverName.visibility = View.GONE
                    receiverMessage.visibility = View.GONE
                    receiverDate.visibility = View.GONE
                } else {
                    // Tampilkan pesan penerima dan sembunyikan pesan pengirim
                    receiverName.text = chat.sender
                    receiverMessage.text = chat.message
                    receiverDate.text = chat.date
                    // Set visibilitas
                    receiverName.visibility = View.VISIBLE
                    receiverMessage.visibility = View.VISIBLE
                    receiverDate.visibility = View.VISIBLE

                    senderName.visibility = View.GONE
                    senderMessage.visibility = View.GONE
                    senderDate.visibility = View.GONE
                }
            }
        }
    }

}
