package com.example.sk8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sk8.databinding.FragmentRegistroBinding


class RegistroFragment : Fragment() {
    private var _binding : FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
         binding.btnRegistrar.setOnClickListener {
             viewModel.requestSignUp(binding.correoTextFieldRegistro.toString().trim(), binding.passwordTextFieldRegistro.toString().trim())
        }
        return binding.root
    }

}