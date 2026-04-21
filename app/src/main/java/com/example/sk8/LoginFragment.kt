package com.example.sk8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sk8.core.FragmentCommunicator
import kotlin.getValue

class LoginFragment : Fragment() {

    //llenar lo que hace falta

    //enlace al viewModel
    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var communicator: FragmentCommunicator
    communicator = requireActivity() as FragmentCommunicator;
}