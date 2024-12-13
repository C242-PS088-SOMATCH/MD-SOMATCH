package com.example.somatchapp.ui.ai_match.prediction.result

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.databinding.FragmentCardMyOutfitBinding

class OutfitStyleAdapter(
    private val context: Context,
    private val outfitStyles: MutableList<OutfitStyle>,
    private val onClickListener: (OutfitStyle) -> Unit
) : RecyclerView.Adapter<OutfitStyleAdapter.OutfitStyleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitStyleViewHolder {
        val binding = FragmentCardMyOutfitBinding.inflate(LayoutInflater.from(context), parent, false)
        return OutfitStyleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OutfitStyleViewHolder, position: Int) {
        val outfitStyle = outfitStyles[position]
        holder.bind(outfitStyle)
    }

    override fun getItemCount(): Int = outfitStyles.size

    inner class OutfitStyleViewHolder(private val binding: FragmentCardMyOutfitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(outfitStyle: OutfitStyle) {
            val imageUrl = outfitStyle.image
            binding.checked.visibility = View.GONE

            // Check if the image URL is null or empty
            if (imageUrl.isNullOrEmpty()) {
                binding.root.visibility = View.GONE
            } else {
                binding.root.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_blank_gallery)
                    .into(binding.myOutfitImage)
            }

            // Set click listener on the card to trigger onClickListener
            binding.root.setOnClickListener {
                onClickListener(outfitStyle)
            }
        }
    }
}