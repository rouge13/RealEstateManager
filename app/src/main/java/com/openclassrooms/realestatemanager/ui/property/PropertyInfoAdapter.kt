package com.openclassrooms.realestatemanager.ui.property

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.realestatemanager.data.model.PhotoEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoAdapter(fragment: Fragment, private val photoList: List<PhotoEntity>?, private val soldOut : Boolean) :
    FragmentStateAdapter(fragment) {

    private val defaultPhoto = PhotoEntity(
        id = -1,
        photo = "ic_default_property", // Replace with the resource name of your default photo
        description = "No photo!" // Replace with the default description
    )

    override fun getItemCount(): Int = photoList?.size!!

    override fun createFragment(position: Int): Fragment {
        val photo = photoList?.get(position) ?: defaultPhoto
        return PhotoFragment(photo, soldOut)
    }
}
