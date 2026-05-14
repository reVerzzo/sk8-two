package com.example.sk8


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sk8.SignInViewModel
import com.example.sk8.core.FragmentCommunicator
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentRegistroBinding
import com.example.sk8.singup.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegistroFragment : Fragment() {
    private var _binding : FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var communicator: FragmentCommunicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnRegistrar.isEnabled = false
        val watcher = { validateAndEnable() }
        binding.correoTiet.addTextChangedListener { validateAndEnable() }
        binding.passwordTiet.addTextChangedListener { validateAndEnable() }
        binding.confirmPasswordTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.correoTiet.text.toString().trim()
        val pass = binding.passwordTiet.text.toString().trim()
        val confirm = binding.confirmPasswordTiet.text.toString().trim()

        binding.correoTil.error = viewModel.validateEmail(email)
        binding.passwordTil.error = viewModel.validatePassword(pass)
        binding.confirmPasswordTil.error =
            viewModel.validateConfirmPassword(pass, confirm)

        binding.btnRegistrar.isEnabled =
            viewModel.isRegisterFormValid(email, pass, confirm)
    }

    private fun setupClickListeners() {
        binding.btnRegistrar.setOnClickListener {
            val email = binding.correoTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            viewModel.requestSignUp(email, password)
        }
        binding.btnRegistrar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRegistrar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            // TODO: navegar a pantalla de datos personales
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnRegistrar.isEnabled = true
                            Snackbar.make(binding.root, state.error,
                                Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

}