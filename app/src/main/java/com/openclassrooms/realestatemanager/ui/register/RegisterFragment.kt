package com.openclassrooms.realestatemanager.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private var registerFragmentListener: RegisterFragmentListener? = null
    private val viewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as MainApplication).agentRepository, (requireActivity().application as MainApplication).propertyRepository)
    }

    companion object {
        const val REGISTER_SUCCESS = "Register success."
        const val REGISTER_MISSING_FIELDS = "Please fill all fields."
        const val REGISTER_FAILED = "Register failed. Wrong password !"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RegisterFragmentListener) {
            registerFragmentListener = context
        } else {
            throw RuntimeException("$context must implement LoginFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        registerFragmentListener = null
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
                    // Insert the new agent in the database
                    val agent = AgentEntity(id = null, firstName = firstName, lastName = lastName, email = email, password = password)
                    // Insert the new agent in the database
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.insertAgent(agent).also {
                            viewModel.setLogedAgent(agent)
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show()
                                registerFragmentListener?.onRegisterSuccess()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, REGISTER_FAILED, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, REGISTER_MISSING_FIELDS, Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            registerFragmentListener?.onRegisterCancel()
        }
    }

}