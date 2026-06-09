package com.example.sk8.home.list

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sk8.R
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentListBinding
import com.example.sk8.model.Spot
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SpotsViewModel>()
    private val auth = FirebaseAuth.getInstance()
    private val adapter = SpotsAdapter(
        onMapClick = { openSpotInMaps(it) },
        onEditClick = { openEditSpot(it) },
        onDeleteClick = { confirmDeleteSpot(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.rvSpots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpots.adapter = adapter
        observeState()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadSpots()
    }

    private fun loadSpots() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showMessage("Sesion invalida. Inicia sesion nuevamente.")
            binding.emptyState.visibility = View.VISIBLE
            binding.rvSpots.visibility = View.GONE
            return
        }
        viewModel.loadSpots(uid)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.spotsState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.emptyState.visibility = View.GONE
                        }
                        is ResponseService.Success -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.submitList(state.data)
                            binding.emptyState.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                            binding.rvSpots.visibility = if (state.data.isEmpty()) View.GONE else View.VISIBLE
                        }
                        is ResponseService.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvSpots.visibility = View.GONE
                            showMessage(state.error)
                        }
                        null -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> showMessage("Spot eliminado")
                        is ResponseService.Error -> showMessage(state.error)
                        ResponseService.Loading, null -> Unit
                    }
                }
            }
        }
    }

    private fun openSpotInMaps(spot: Spot) {
        val uri = Uri.parse("geo:${spot.latitude},${spot.longitude}?q=${spot.latitude},${spot.longitude}(${Uri.encode(spot.name)})")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (fallback: ActivityNotFoundException) {
                showMessage("No hay una app de mapas instalada.")
            }
        }
    }

    private fun openEditSpot(spot: Spot) {
        findNavController().navigate(
            R.id.addFragment,
            bundleOf(
                "spotId" to spot.id,
                "spotName" to spot.name,
                "spotDescription" to spot.description,
                "spotLatitude" to spot.latitude,
                "spotLongitude" to spot.longitude,
                "spotCreatedAt" to spot.createdAt
            )
        )
    }

    private fun confirmDeleteSpot(spot: Spot) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar spot")
            .setMessage("Esta accion eliminara ${spot.name}.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                auth.currentUser?.uid?.let { viewModel.deleteSpot(spot, it) }
            }
            .show()
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
