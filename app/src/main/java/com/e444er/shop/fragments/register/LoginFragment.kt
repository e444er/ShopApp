package com.e444er.shop.fragments.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.e444er.shop.R
import com.e444er.shop.activities.MainActivity
import com.e444er.shop.databinding.FragmentLoginBinding
import com.e444er.shop.util.Resource
import com.e444er.shop.util.setupBottomDialog
import com.e444er.shop.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forget.setOnClickListener {
            setupBottomDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.apply {
            btnLogin.setOnClickListener {
                val email = email.text.toString().trim()
                val password = password.text.toString().trim()
                viewModel.login(email, password)
            }
        }


        lifecycleScope.launchWhenCreated {
            viewModel.resetPassword.collect {
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                       Snackbar.make(requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.login.collect {
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        Intent(requireActivity(), MainActivity::class.java).also {intent->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }



}