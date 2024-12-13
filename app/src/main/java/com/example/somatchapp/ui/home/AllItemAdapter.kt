package com.example.somatchapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.somatchapp.data.remote.response.AllItemResponse
import com.example.somatchapp.databinding.FragmentCardProductBinding

class AllItemAdapter :
    ListAdapter<AllItemResponse, AllItemAdapter.AllItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllItemViewHolder {
        val binding = FragmentCardProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AllItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class AllItemViewHolder(private val binding: FragmentCardProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllItemResponse) {
            // Remove underscores and capitalize the first letter of each word
            val formattedName = item.name.replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.capitalize() }

            binding.productName.text = formattedName
            binding.productType.text = item.category
            binding.productHashtag.text = "#${item.color_group}"

            // Using Glide to load the image
            Glide.with(binding.productImage.context)
                .load(item.image_url)
                .into(binding.productImage)

            binding.root.setOnClickListener {
                // Handle item click if needed
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AllItemResponse>() {
        override fun areItemsTheSame(oldItem: AllItemResponse, newItem: AllItemResponse): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: AllItemResponse, newItem: AllItemResponse): Boolean {
            return oldItem == newItem
        }
    }
}
