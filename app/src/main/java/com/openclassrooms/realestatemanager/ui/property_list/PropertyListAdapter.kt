package com.openclassrooms.realestatemanager.ui.property_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.data.model.PropertyEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListAdapter(diffCallback: DiffUtil.ItemCallback<PropertyEntity>)
    : ListAdapter<PropertyEntity, PropertyListAdapter.PropertyViewHolder>(diffCallback) {

    class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(get: PropertyEntity) {
            // Set the data to the view
            binding.propertyType.text = get.typeOfHouse
            binding.propertySector.text = get.boroughs
            "$${get.price}".also { binding.propertyValue.text = it }
            // Set the image to the view
            setImageInRecyclerView(get)
        }

        private fun setImageInRecyclerView(get: PropertyEntity) {
            val context = binding.root.context
            val id =
                context.resources.getIdentifier(get.primaryPhoto, "drawable", context.packageName)
            binding.propertyImage.setImageResource(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPropertyBinding.inflate(inflater, parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = getItem(position)
        holder.bind(property)
    }
}

