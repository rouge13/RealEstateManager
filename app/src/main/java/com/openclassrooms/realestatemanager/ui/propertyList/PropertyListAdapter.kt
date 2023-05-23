package com.openclassrooms.realestatemanager.ui.propertyList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import android.util.Log

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListAdapter(diffCallback: DiffUtil.ItemCallback<PropertyWithDetails>, private val onPropertyClick: (PropertyWithDetails) -> Unit)
    : ListAdapter<PropertyWithDetails, PropertyListAdapter.PropertyViewHolder>(diffCallback) {

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(get: PropertyWithDetails) {
            // Set the data to the view
            binding.propertyType.text = get.property.typeOfHouse
            binding.propertySector.text = get.address?.boroughs?.takeIf { it.isNotBlank() } ?: "N/A"
            "$${get.property.price}".also { binding.propertyValue.text = it }

            // Set the image to the view
            setImageInRecyclerView(get.property)

            val propertyLayout = binding.propertyLayout
            if (get.property.isSold == true) {
                propertyLayout.alpha = 0.3f
                binding.soldText.visibility = android.view.View.VISIBLE
            } else {
                propertyLayout.alpha = 1f
                binding.soldText.visibility = android.view.View.GONE
            }

            // Set the onClickListener
            itemView.setOnClickListener {
                val position = this.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val propertyWithDetails = getItem(position)
                    Log.d("PropertyListAdapter", "Item clicked: ${propertyWithDetails.property.id}")
                    onPropertyClick(propertyWithDetails)
                }
            }
        }

        private fun setImageInRecyclerView(get: PropertyEntity) {
            val context = binding.root.context
            val id = context.resources.getIdentifier(get.primaryPhoto, "drawable", context.packageName)
            binding.propertyImage.setImageResource(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPropertyBinding.inflate(inflater, parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val propertyWithDetails = getItem(position)
        holder.bind(propertyWithDetails)
    }
}