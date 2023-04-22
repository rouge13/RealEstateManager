package com.openclassrooms.realestatemanager.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController


import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentLoginBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.property_list.PropertyListFragmentDirections
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory(
            (requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository
        )
    }

    companion object {
        const val LOGIN_SUCCESS = "login success."
        const val LOGIN_MISSING_FIELDS = "Please fill all fields."
        const val LOGIN_FAILED = "Login failed. Wrong email or password !"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnClickListener {
            val email = binding.loginEmailEdit.text.toString()
            val password = binding.loginPasswordEdit.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkIfAgentCanLogOn(email, password)
            } else {
                Toast.makeText(requireContext(), LOGIN_MISSING_FIELDS, Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToPropertyListFragment()
            binding.root.findNavController().navigate(action)
        }
        binding.signUpBtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun checkIfAgentCanLogOn(email: String, password: String) {
        viewModel.agentData(
            email,
            password
        ).observe(viewLifecycleOwner) { agent ->
            if (agent != null && agent.email == email && agent.password == password) {
                Toast.makeText(
                    requireContext(),
                    " $LOGIN_SUCCESS Welcome ${agent.firstName} ${agent.lastName}",
                    Toast.LENGTH_SHORT
                ).show()
                // Set the logged-in agent in the ViewModel
                viewModel.setLogedAgent(agent)
                // Notify the listener that the login was successful
                val action = LoginFragmentDirections.actionLoginFragmentToPropertyListFragment()
                binding.root.findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), LOGIN_FAILED, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}