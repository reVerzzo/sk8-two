package com.example.sk8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sk8.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistrarse.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registroFragment)
        }

        // Acción: De Login a la pantalla de Recuperar Contraseña
        binding.btnReestablecer.setOnClickListener {
            // Reemplaza "action_loginFragment_to_recuperarCFragment" si tu flecha tiene otro ID
            findNavController().navigate(R.id.action_loginFragment_to_recuperarCFragment)
        }

        binding.button.setOnClickListener {

            println("¡Intento de inicio de sesión del rider!")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}