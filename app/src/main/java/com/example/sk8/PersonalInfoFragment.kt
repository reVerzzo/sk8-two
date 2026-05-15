package com.example.sk8

import android.app.DatePickerDialog
import android.content.Intent
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
import com.example.sk8.core.FragmentCommunicator
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentPersonalInfoBinding
import com.example.sk8.home.HomeActivity
import com.example.sk8.model.PersonalInfoViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonalInfoViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupDatePicker()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnRegistrarInfo.isEnabled = false
        binding.etNombre.addTextChangedListener { validateAndEnable() }
        binding.etApellidos.addTextChangedListener { validateAndEnable() }
        binding.etUsername.addTextChangedListener { validateAndEnable() }
        binding.etTelefono.addTextChangedListener { validateAndEnable() }
        binding.etFechaNac.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val firstName = binding.etNombre.text.toString().trim()
        val lastName = binding.etApellidos.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val phone = binding.etTelefono.text.toString().trim()
        val birthDate = binding.etFechaNac.text.toString().trim()

        binding.tilNombre.error = viewModel.validateFirstName(firstName)
        binding.tilApellidos.error = viewModel.validateLastName(lastName)
        binding.tilUsername.error = viewModel.validateUsername(username)
        binding.tilTelefono.error = viewModel.validatePhone(phone)
        binding.tilFechaNac.error = viewModel.validateBirthDate(birthDate)

        binding.btnRegistrarInfo.isEnabled =
            viewModel.isFormValid(firstName, lastName, username, phone, birthDate)
    }

    private fun setupDatePicker() {
        binding.etFechaNac.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etFechaNac.setText("%04d-%02d-%02d".format(year, month + 1, day))
                },
                cal.get(Calendar.YEAR) - 18,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
    }

    private fun setupClickListeners() {
        binding.btnRegistrarInfo.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Snackbar.make(binding.root, "Sesion invalida", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveProfile(
                uid = uid,
                firstName = binding.etNombre.text.toString().trim(),
                lastName = binding.etApellidos.text.toString().trim(),
                username = binding.etUsername.text.toString().trim(),
                phone = binding.etTelefono.text.toString().trim(),
                birthDate = binding.etFechaNac.text.toString().trim()
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRegistrarInfo.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            val intent = Intent(requireContext(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnRegistrarInfo.isEnabled = true
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
