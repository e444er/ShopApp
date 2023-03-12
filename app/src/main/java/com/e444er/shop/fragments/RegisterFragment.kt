package com.e444er.shop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.e444er.shop.R
import com.e444er.shop.data.User
import com.e444er.shop.databinding.FragmentRegisterBinding
import com.e444er.shop.util.RegisterValidation
import com.e444er.shop.util.Resource
import com.e444er.shop.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val user = User(
                    edFirstName.text.toString().trim(),
                    lastName.text.toString().trim(),
                    edEmail.text.toString().trim(),
                )
                val password = edPassword.text.toString().trim()
                viewModel.registerNewUser(user, password)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when (it) {
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {

                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect {
                    if (it.email is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edEmail.apply {
                                requestFocus()
                                error = it.email.message
                            }
                        }
                    }
                    if (it.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edPassword.apply {
                                requestFocus()
                                error = it.password.message
                            }
                        }
                    }
                }
            }
        }
    }
}