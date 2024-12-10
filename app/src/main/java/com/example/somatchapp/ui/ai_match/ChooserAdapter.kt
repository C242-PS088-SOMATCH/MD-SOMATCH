package com.example.somatchapp.ui.ai_match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.somatchapp.data.local.entity.ChooserCardData
import com.example.somatchapp.databinding.FragmentHorizonCardBinding

class ChooserAdapter(
    private val items: List<ChooserCardData>,
    private val navController: NavController // Pass NavController
) : RecyclerView.Adapter<ChooserAdapter.DeciderViewHolder>() {

    inner class DeciderViewHolder(private val binding: FragmentHorizonCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChooserCardData) {
            binding.cardImage.setImageResource(item.imageResId)
            binding.title.text = item.title
            binding.descriptions.text = item.description

            binding.cardButton.setOnClickListener {
                navController.navigate(item.navigateLink) // Navigate to the appropriate fragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeciderViewHolder {
        val binding = FragmentHorizonCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeciderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeciderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

