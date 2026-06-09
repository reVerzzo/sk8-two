package com.example.sk8.home.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sk8.databinding.ItemSpotBinding
import com.example.sk8.model.Spot

class SpotsAdapter(
    private val onMapClick: (Spot) -> Unit,
    private val onEditClick: (Spot) -> Unit,
    private val onDeleteClick: (Spot) -> Unit
) : ListAdapter<Spot, SpotsAdapter.SpotViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val binding = ItemSpotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SpotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SpotViewHolder(
        private val binding: ItemSpotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(spot: Spot) {
            binding.tvSpotName.text = spot.name
            binding.tvSpotDescription.text = spot.description
            binding.tvSpotLocation.text = "Lat: %.5f, Lng: %.5f".format(spot.latitude, spot.longitude)
            binding.root.setOnClickListener { onMapClick(spot) }
            binding.editSpotButton.setOnClickListener { onEditClick(spot) }
            binding.deleteSpotButton.setOnClickListener { onDeleteClick(spot) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Spot>() {
            override fun areItemsTheSame(oldItem: Spot, newItem: Spot) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Spot, newItem: Spot) = oldItem == newItem
        }
    }
}
