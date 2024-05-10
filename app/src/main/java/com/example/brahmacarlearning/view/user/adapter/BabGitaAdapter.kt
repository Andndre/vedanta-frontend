package com.example.brahmacarlearning.view.user.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.brahmacarlearning.data.remote.response.BabsItem
import com.example.brahmacarlearning.databinding.ItemGitaBinding
import com.example.brahmacarlearning.view.user.detail.DetailActivity

class BabGitaAdapter(private val context: Context) : PagingDataAdapter<BabsItem, BabGitaAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sloka = getItem(position)
        if (sloka != null){
            holder.bind(sloka)
            holder.itemView.setOnClickListener{
                val moveGitaDataIntent = Intent(holder.itemView.context, DetailActivity::class.java)
                moveGitaDataIntent.putExtra(DetailActivity.BAB, sloka.number.toString())
                context.startActivity(moveGitaDataIntent)
            }
        }
    }

    class MyViewHolder(val binding: ItemGitaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sloka: BabsItem) {
            with(binding) {
                tvItemName.text = sloka.title
                tvItemDescription.text = sloka.summary

            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BabsItem>() {
            override fun areItemsTheSame(oldItem: BabsItem, newItem: BabsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BabsItem,
                newItem: BabsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
