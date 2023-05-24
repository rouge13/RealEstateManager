package com.openclassrooms.realestatemanager.ui.propertyList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.databinding.FragmentPropertyListBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.property.PropertyInfoFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListFragment : Fragment() {
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var adapter: PropertyListAdapter
    private val propertyListViewModel: SharedPropertyViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        // Configure the adapter
        adapter = PropertyListAdapter(object : DiffUtil.ItemCallback<PropertyWithDetails>() {
            override fun areItemsTheSame(oldItem: PropertyWithDetails, newItem: PropertyWithDetails): Boolean {
                return oldItem.property.id == newItem.property.id
            }
            override fun areContentsTheSame(oldItem: PropertyWithDetails, newItem: PropertyWithDetails): Boolean {
                return oldItem.property == newItem.property &&
                        oldItem.address == newItem.address &&
                        oldItem.photos == newItem.photos
            }
        }, onPropertyClick = { propertyWithDetails ->
            navigateToInfoPropertyFragment(propertyWithDetails)
        })
        // Set the layout manager for the recyclerView
        binding.fragmentPropertyListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set the adapter for the recyclerView
        binding.fragmentPropertyListRecyclerView.adapter = adapter
        // Configure the recyclerView
        configureRecyclerView()
        initSharedNavigationViewModelSearchAction()
        return binding.root
    }
    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = PropertyListFragmentDirections.actionPropertyListFragmentToSearchFragment()
                findNavController().navigate(action)
                sharedNavigationViewModel.doneNavigatingToSearch()
                // If we are navigating to the search fragment, we should set the SearchCriteria to null
                // so that all properties are fetched
                propertyListViewModel.setSearchCriteria(null)
            }
        }
    }
    private fun configureRecyclerView() {
        // Observe the combined data from the ViewModel
        propertyListViewModel.getPropertiesWithDetails.observe(viewLifecycleOwner) { propertiesWithDetails ->
            adapter.submitList(propertiesWithDetails)
        }
    }
    private fun navigateToInfoPropertyFragment(propertyWithDetails: PropertyWithDetails) {
        propertyListViewModel.setSelectProperty(propertyWithDetails)
        if (!isDualPanel()) {
//            val action = PropertyListFragmentDirections.actionPropertyListFragmentToInfoPropertyFragment()
            binding.root.findNavController().navigate(R.id.infoPropertyFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isDualPanel()) {
            childFragmentManager.beginTransaction()
                .replace(R.id.info_fragment_container, PropertyInfoFragment())
                .commit()
        }
    }

    private fun isDualPanel(): Boolean {
        return resources.getBoolean(R.bool.isTwoPanel)
    }
}