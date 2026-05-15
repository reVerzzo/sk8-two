package com.example.sk8.home.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sk8.MainActivity
import com.example.sk8.core.ResponseService
import com.example.sk8.databinding.FragmentAccountBinding
import com.example.sk8.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val repository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        setupClickListeners()
        loadProfile()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid == null) {
            binding.progressBar.visibility = View.GONE
            binding.profileText.text = "Sesion invalida"
            return
        }

        binding.profileText.text = buildAuthProfileText(user.email)

        viewLifecycleOwner.lifecycleScope.launch {
            when (val response = repository.getUserInfo(uid)) {
                is ResponseService.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val profile = response.data
                    binding.profileText.text = """
                        Skater: ${profile.firstName} ${profile.lastName}
                        Usuario: ${profile.userName}
                        Telefono: ${profile.phone}
                        Nacimiento: ${profile.birthDate}
                    """.trimIndent()
                }
                is ResponseService.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.profileText.text = """
                        ${buildAuthProfileText(user.email)}

                        No se pudo cargar el perfil guardado.
                        Revisa las reglas de Firestore para permitir lectura a usuarios autenticados.
                    """.trimIndent()
                }
                ResponseService.Loading -> Unit
            }
        }
    }

    private fun buildAuthProfileText(email: String?): String {
        return """
            Usuario autenticado
            Correo: ${email ?: "Sin correo"}
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
