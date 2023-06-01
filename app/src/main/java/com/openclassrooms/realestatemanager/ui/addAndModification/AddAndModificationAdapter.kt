package com.openclassrooms.realestatemanager.ui.addAndModification

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationAdapter(private val photos: MutableList<Drawable?>) : RecyclerView.Adapter<AddAndModificationAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    fun addPhoto(drawable: Drawable) {
        photos.add(drawable)
        notifyItemInserted(photos.size - 1)
    }

    override fun getItemCount(): Int = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    photos.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }

        fun bind(photo: Drawable?) {
            binding.imageView.setImageDrawable(photo)
        }
    }
}