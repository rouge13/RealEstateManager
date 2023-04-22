package com.openclassrooms.realestatemanager.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.databinding.FragmentRegisterBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory(
            (requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository
        )
    }

    companion object {
        const val REGISTER_SUCCESS = "Register success."
        const val REGISTER_MISSING_FIELDS = "Please fill all fields."
        const val REGISTER_FAILED = "Register failed. Wrong password !"
        const val EMAIL_ALREADY_EXISTS = "Email already exists."
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerBtn.setOnClickListener {
            val lastName = binding.registerLastName.text.toString()
            val firstName = binding.registerFirstName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val passwordConfirm = binding.registerPasswordConfirm.text.toString()
            if (lastName.isNotEmpty() && firstName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
                if (password == passwordConfirm) {
                    createAgentAccountIfTheEmailDoenstAlreadyExists(email, firstName, lastName, password)
                } else {
                    Toast.makeText(context, REGISTER_FAILED, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, REGISTER_MISSING_FIELDS, Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun createAgentAccountIfTheEmailDoenstAlreadyExists(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) {
        viewModel.viewModelScope.launch {
            val existingAgent = viewModel.getAgentByEmail(email)
            if (existingAgent == null) {
                // Email is not in the database, proceed with registration
                val agent = AgentEntity(id = null, firstName = firstName, lastName = lastName, email = email, password = password)
                viewModel.insertAgent(agent)
                viewModel.setLogedAgent(agent)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show()
                    val action = RegisterFragmentDirections.actionRegisterFragmentToPropertyListFragment()
                    binding.root.findNavController().navigate(action)
                }
            } else {
                // Email is already in the database, show an error message
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, EMAIL_ALREADY_EXISTS, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}