package com.example.sk8.home.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sk8.R
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentAddBinding
import com.example.sk8.model.Spot
import com.example.sk8.repositories.SpotRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val repository = SpotRepository()
    private val auth = FirebaseAuth.getInstance()
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }

    private var editingSpot: Spot? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                saveNewSpotWithLocation()
            } else {
                setLoading(false)
                showMessage("Necesitamos permiso de ubicacion para registrar el spot.")
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        setupEditState()
        setupValidation()
        setupClickListeners()
        return binding.root
    }

    private fun setupEditState() {
        val spotId = arguments?.getString("spotId")
        if (spotId != null) {
            editingSpot = Spot(
                id = spotId,
                name = arguments?.getString("spotName").orEmpty(),
                description = arguments?.getString("spotDescription").orEmpty(),
                latitude = arguments?.getDouble("spotLatitude") ?: 0.0,
                longitude = arguments?.getDouble("spotLongitude") ?: 0.0,
                createdBy = auth.currentUser?.uid.orEmpty(),
                createdAt = arguments?.getLong("spotCreatedAt") ?: 0L
            )
            binding.titleText.text = "Editar spot"
            binding.saveSpotButton.text = "Actualizar spot"
            binding.nameTiet.setText(editingSpot?.name)
            binding.descriptionTiet.setText(editingSpot?.description)
        }
    }

    private fun setupValidation() {
        binding.saveSpotButton.isEnabled = false
        binding.nameTiet.addTextChangedListener { validateForm() }
        binding.descriptionTiet.addTextChangedListener { validateForm() }
        validateForm()
    }

    private fun validateForm(): Boolean {
        val name = binding.nameTiet.text.toString().trim()
        val description = binding.descriptionTiet.text.toString().trim()

        binding.nameTil.error = if (name.isBlank()) "El nombre es requerido" else null
        binding.descriptionTil.error = if (description.isBlank()) "La descripcion es requerida" else null

        val isValid = name.isNotBlank() && description.isNotBlank()
        binding.saveSpotButton.isEnabled = isValid
        return isValid
    }

    private fun setupClickListeners() {
        binding.saveSpotButton.setOnClickListener {
            if (!validateForm()) {
                showMessage("Completa los campos obligatorios.")
                return@setOnClickListener
            }

            if (editingSpot != null) {
                updateSpot()
            } else {
                requestLocationAndSave()
            }
        }
    }

    private fun requestLocationAndSave() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            saveNewSpotWithLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveNewSpotWithLocation() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showMessage("Sesion invalida. Inicia sesion nuevamente.")
            return
        }

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val location = locationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                    .await()

                if (location == null) {
                    setLoading(false)
                    showMessage("No se pudo obtener la ubicacion actual. Intenta de nuevo.")
                    return@launch
                }

                when (val response = repository.createSpot(
                    userId = uid,
                    name = binding.nameTiet.text.toString().trim(),
                    description = binding.descriptionTiet.text.toString().trim(),
                    latitude = location.latitude,
                    longitude = location.longitude
                )) {
                    is ResponseService.Success -> {
                        setLoading(false)
                        showMessage("Spot guardado")
                        findNavController().navigate(R.id.listFragment)
                    }
                    is ResponseService.Error -> {
                        setLoading(false)
                        showMessage(response.error)
                    }
                    ResponseService.Loading -> Unit
                }
            } catch (e: Exception) {
                setLoading(false)
                showMessage("No se pudo obtener la ubicacion: ${e.localizedMessage}")
            }
        }
    }

    private fun updateSpot() {
        val spot = editingSpot ?: return
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            when (val response = repository.updateSpot(
                spot = spot,
                name = binding.nameTiet.text.toString().trim(),
                description = binding.descriptionTiet.text.toString().trim()
            )) {
                is ResponseService.Success -> {
                    setLoading(false)
                    showMessage("Spot actualizado")
                    findNavController().navigate(R.id.listFragment)
                }
                is ResponseService.Error -> {
                    setLoading(false)
                    showMessage(response.error)
                }
                ResponseService.Loading -> Unit
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.saveSpotButton.isEnabled = !isLoading && validateForm()
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
