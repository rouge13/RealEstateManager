package com.openclassrooms.realestatemanager.ui.property_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestatemanager.databinding.FragmentPropertyListBinding
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.ui.MainApplication

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListFragment : Fragment() {
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var adapter: PropertyListAdapter
    private val propertyListViewModel: PropertyListViewModel by viewModels {
        PropertyListModelFactory((requireActivity().application as MainApplication).propertyRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        // Configure the adapter
        adapter = PropertyListAdapter(object : DiffUtil.ItemCallback<PropertyEntity>() {
            override fun areItemsTheSame(oldItem: PropertyEntity, newItem: PropertyEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: PropertyEntity, newItem: PropertyEntity): Boolean {
                return oldItem == newItem
            }
        })
        // Set the layout manager for the recyclerView
        binding.fragmentPropertyListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set the adapter for the recyclerView
        binding.fragmentPropertyListRecyclerView.adapter = adapter
        // Configure the recyclerView
        configureRecyclerView()
        return binding.root
    }

    private fun configureRecyclerView() {
        propertyListViewModel.allProperties.observe(viewLifecycleOwner) { properties ->
            adapter.submitList(properties)
        }
    }
}