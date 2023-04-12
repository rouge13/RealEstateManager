package com.openclassrooms.realestatemanager.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.realestatemanager.databinding.FragmentRegisterBinding
import com.openclassrooms.realestatemanager.ui.login.LoginFragmentListener

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    //    private val viewModel: RegisterViewModel by activityViewModels {
//        RegisterViewModelFactory((requireActivity().application as MainApplication).agentRepository)
//    }
    private var registerFragmentListener: RegisterFragmentListener? = null

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

                }
            }
        }
        binding.btnCancel.setOnClickListener {
            registerFragmentListener?.onRegisterCancel()
        }
    }

}