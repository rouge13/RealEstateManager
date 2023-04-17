package com.openclassrooms.realestatemanager.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentLoginBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
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
            (requireActivity().application as MainApplication).proximityRepository
        )
    }

    companion object {
        const val LOGIN_SUCCESS = "login success."
        const val LOGIN_MISSING_FIELDS = "Please fill all fields."
        const val LOGIN_FAILED = "Login failed. Wrong email or password !"
    }

    private var loginFragmentListener: LoginFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginFragmentListener) {
            loginFragmentListener = context
        } else {
            throw RuntimeException("$context must implement LoginFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginFragmentListener = null
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
            loginFragmentListener?.onLoginCancel()
        }
        binding.signUpBtn.setOnClickListener {
            loginFragmentListener?.onLoginSignUp()
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
                loginFragmentListener?.onLoginSuccess()
            } else {
                Toast.makeText(requireContext(), LOGIN_FAILED, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}