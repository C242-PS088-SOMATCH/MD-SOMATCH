package com.example.somatchapp.ui.my_catalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.MyCatalog
import com.example.somatchapp.databinding.FragmentCardMyOutfitBinding

class MyCatalogAdapter(
    private var myCatalogList: List<MyCatalog>,
    private val context: Context,
    private val onItemClick: (MyCatalog) -> Unit,
    private val itemType: String
) : RecyclerView.Adapter<MyCatalogAdapter.MyCatalogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCatalogViewHolder {
        val binding = FragmentCardMyOutfitBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyCatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCatalogViewHolder, position: Int) {
        val myCatalog = myCatalogList[position]
        // Load image using Glide
        Glide.with(context)
            .load(myCatalog.image)
            .placeholder(R.drawable.image_recommendation_outfit_illustration)
            .into(holder.binding.myOutfitImage)

        holder.binding.checked.visibility = if (myCatalog.isSelected) View.VISIBLE else View.GONE

        if ( itemType != "empty" ) {
            holder.itemView.setOnClickListener {
                updateSelection(myCatalog)
                onItemClick(myCatalog)
            }
        }
    }

    override fun getItemCount(): Int = myCatalogList.size

    private fun updateSelection(selectedItem: MyCatalog) {
        myCatalogList = myCatalogList.map {
            it.copy(isSelected = it.id == selectedItem.id)
        }
        notifyDataSetChanged()
    }

    fun updateData(newData: List<MyCatalog>) {
        myCatalogList = newData
        notifyDataSetChanged()
    }

    class MyCatalogViewHolder(val binding: FragmentCardMyOutfitBinding) : RecyclerView.ViewHolder(binding.root)
}