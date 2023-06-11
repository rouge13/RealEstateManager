package com.openclassrooms.realestatemanager.ui.propertyList

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListAdapter(private val lifecycleOwner: LifecycleOwner, private val sharedPropertyViewModel: SharedPropertyViewModel, private val sharedUtilsViewModel: SharedUtilsViewModel, diffCallback: DiffUtil.ItemCallback<PropertyWithDetails>, private val onPropertyClick: (PropertyWithDetails) -> Unit)
    : ListAdapter<PropertyWithDetails, PropertyListAdapter.PropertyViewHolder>(diffCallback) {

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(get: PropertyWithDetails) {
            // Set the data to the view
            binding.propertyType.text = get.property.typeOfHouse
            binding.propertySector.text = get.address?.boroughs?.takeIf { it.isNotBlank() }

//            "${get.property.price}".also { binding.propertyValue.text = it }

            // Use coroutine scope to collect the value of getMoneyRateSelected
            CoroutineScope(Dispatchers.Main).launch {
                sharedUtilsViewModel.getMoneyRateSelected.observe(lifecycleOwner) { isEuroSelected ->
                    val convertedPrice = sharedPropertyViewModel.convertPropertyPrice(get.property, isEuroSelected)
                    binding.propertyValue.text = when (convertedPrice) {
                        is Int -> {
                            if (isEuroSelected) {
                                "$convertedPrice€"
                            } else {
                                "$$convertedPrice"
                            }
                        }
                        else -> "$${get.property.price}" // Handle the case when price or conversion is null
                    }
                }
            }

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
            if (get.primaryPhoto == null) {
                val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                binding.propertyImage.setImageResource(defaultImageId)
            } else {
                val resourceId = context.resources.getIdentifier(get.primaryPhoto, "drawable", context.packageName)
                if (resourceId != 0) {
                    // The primary photo is a drawable resource
                    binding.propertyImage.setImageResource(resourceId)
                } else {
                    // The primary photo is a URI
                    try {
                        val uri = Uri.parse(get.primaryPhoto)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.propertyImage.setImageBitmap(bitmap)
                        } else {
                            // Failed to load the image from URI, use default image
                            val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                            binding.propertyImage.setImageResource(defaultImageId)
                        }
                    } catch (e: Exception) {
                        // Error loading the image from URI, use default image
                        val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                        binding.propertyImage.setImageResource(defaultImageId)
                    }
                }
            }
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
