package com.openclassrooms.realestatemanager.ui.addAndModification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentAddAndModifyPropertyBinding
import com.openclassrooms.realestatemanager.databinding.FragmentInfoPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationFragment: Fragment() {
    private lateinit var binding: FragmentAddAndModifyPropertyBinding
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }

    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAndModifyPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedNavigationViewModel.getAddOrModifyClicked.observe(viewLifecycleOwner) { isModify ->
            if (isModify) {
                binding.propertySwitchSold.visibility = View.VISIBLE
//                displayPropertyDetails(view)
            } else {
                binding.propertySwitchSold.visibility = View.GONE
            }
        }
    }
}