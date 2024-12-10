package com.example.somatchapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.TrendFashion
import com.example.somatchapp.databinding.FragmentCardTrendingBinding

class TrendFashionAdapter(
    private val trendList: List<TrendFashion>
) : RecyclerView.Adapter<TrendFashionAdapter.TrendViewHolder>() {

    inner class TrendViewHolder(private val binding: FragmentCardTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trend: TrendFashion) {
            binding.trendImage.setImageResource(trend.imageResId)
            binding.styleName.text = trend.title
            binding.styleHashtag.text = trend.colors
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendViewHolder {
        val binding = FragmentCardTrendingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendViewHolder, position: Int) {
        holder.bind(trendList[position])
    }

    override fun getItemCount(): Int = trendList.size
}
