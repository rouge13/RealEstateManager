package com.openclassrooms.realestatemanager.ui.addAndModification

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationAdapter(
    private val photoList: MutableList<PhotoEntity>,
    private val onDeletePhoto: (position: Int) -> Unit
) : RecyclerView.Adapter<AddAndModificationAdapter.PhotoViewHolder>() {

    private val photos: MutableList<Pair<PhotoEntity, Drawable?>> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val (photoEntity, drawable) = photos[position]
        holder.bind(photoEntity, drawable)
    }

    fun addPhoto(photoEntity: PhotoEntity, drawable: Drawable?) {
        photos.add(photoEntity to drawable)
        notifyItemInserted(photos.size - 1)
    }

    fun removePhoto(position: Int) {
        photos.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updatePhotos(drawableList: List<Drawable?>) {
        photos.clear()
        photos.addAll(photoList.zip(drawableList))
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeletePhoto(position) // Call onDeletePhoto callback
                }
            }
        }

        fun bind(photoEntity: PhotoEntity, drawable: Drawable?) {
            binding.imageView.setImageDrawable(drawable)
        }
    }

    fun getDrawableFromPhotoEntity(context: Context, photoEntity: PhotoEntity): Drawable? {
        return if (photoEntity.photo?.startsWith("ic_") == true) {
            // Photo from resources
            val resourceId = context.resources.getIdentifier(
                photoEntity.photo,
                "drawable",
                context.packageName
            )
            try {
                ContextCompat.getDrawable(context, resourceId)
            } catch (e: Exception) {
                null
            }
        } else {
            // Photo from URI
            val uri = Uri.parse(photoEntity.photo)
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    Drawable.createFromStream(it, uri.toString())
                }
            } catch (e: Exception) {
                null
            }
        }
    }

}