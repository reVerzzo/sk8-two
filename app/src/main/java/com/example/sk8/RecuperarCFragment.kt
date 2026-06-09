package com.example.sk8

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sk8.core.AuthRepository
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentRecuperarCBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RecuperarCFragment : Fragment() {
    private var _binding: FragmentRecuperarCBinding? = null
    private val binding get() = _binding!!
    private val repository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperarCBinding.inflate(inflater, container, false)
        setupValidation()
        setupClickListeners()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnRecuperar.isEnabled = false
        binding.emailTiet.addTextChangedListener {
            val email = binding.emailTiet.text.toString().trim()
            binding.tilEmail.error = validateEmail(email)
            binding.btnRecuperar.isEnabled = validateEmail(email) == null
        }
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRecuperar.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val error = validateEmail(email)
            if (error != null) {
                binding.tilEmail.error = error
                return@setOnClickListener
            }
            requestPasswordReset(email)
        }
    }

    private fun requestPasswordReset(email: String) {
        binding.btnRecuperar.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            when (val response = repository.requestPasswordReset(email)) {
                is ResponseService.Success -> {
                    showMessage("Te enviamos un correo para restablecer tu contrasena.")
                    findNavController().navigateUp()
                }
                is ResponseService.Error -> {
                    binding.btnRecuperar.isEnabled = true
                    showMessage(response.error)
                }
                ResponseService.Loading -> Unit
            }
        }
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es requerido"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Correo invalido"
        return null
    }

    private fun showMessage(message: String) {
        if (_binding != null) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
