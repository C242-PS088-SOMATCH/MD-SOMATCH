package com.example.somatchapp.ui.my_catalog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.somatchapp.R
import com.example.somatchapp.data.local.dao.OutfitStyleDao
import com.example.somatchapp.data.remote.response.MyCatalog
import com.example.somatchapp.databinding.FragmentCardMyOutfitBinding

class MyCatalogAdapter(
    private var myCatalogList: List<MyCatalog>,
    private val context: Context,
    private val itemType: String,
    private val outfitStyleDao: OutfitStyleDao,
    private val onItemClick: ((MyCatalog) -> Unit)? = null
) : RecyclerView.Adapter<MyCatalogAdapter.MyCatalogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCatalogViewHolder {
        val binding = FragmentCardMyOutfitBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyCatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCatalogViewHolder, position: Int) {
        val myCatalog = myCatalogList[position]

        // Load image using Glide
        Glide.with(context)
            .load(myCatalog.image_url)
            .placeholder(R.drawable.ic_blank_gallery)
            .into(holder.binding.myOutfitImage)

        // Get SharedPreferences
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val selectedId = sharedPreferences.getInt("selected_id", -1)

        // Set visibility of checked icon based on selected ID
        holder.binding.checked.visibility =
            if (myCatalog.id == selectedId) View.VISIBLE else View.GONE

        // Handle item click
        holder.itemView.setOnClickListener {
            if (itemType != "empty") {
                val editor = sharedPreferences.edit()
                editor.putInt("selected_id", myCatalog.id)
                editor.putString("selected_image_url", myCatalog.image_url)
                editor.apply()
                notifyDataSetChanged() // Refresh UI to reflect changes
            }
            onItemClick?.invoke(myCatalog)
        }
    }

    override fun getItemCount(): Int {
        return if (itemType == "empty") {
            myCatalogList.size
        } else {
            // Filter list berdasarkan kondisi myCatalogId
            val outfitStyles = outfitStyleDao.getAllOutfitStyles().value ?: emptyList()
            val filteredList = myCatalogList.filter { catalog ->
                outfitStyles.none { it.myCatalogId == catalog.id }
            }
            filteredList.size
        }
    }

    fun updateData(newData: List<MyCatalog>) {
        myCatalogList = newData
        notifyDataSetChanged()
    }

    fun clearSelectedId() {
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val token = sharedPreferences.getString("token", null)

        editor.putString("token", token)
        editor.putInt("selected_id", 0)
        editor.putString("selected_image_url", "empty")
        editor.apply()
    }

    class MyCatalogViewHolder(val binding: FragmentCardMyOutfitBinding) : RecyclerView.ViewHolder(binding.root)
}