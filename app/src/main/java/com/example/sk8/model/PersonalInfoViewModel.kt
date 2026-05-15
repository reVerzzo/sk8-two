package com.example.sk8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sk8.core.ResponseService
import com.example.sk8.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel: ViewModel() {
    private val repository = UserRepository()
    private val _saveState = MutableStateFlow<ResponseService<Unit>?>(null)
    val saveState: StateFlow<ResponseService<Unit>?> = _saveState.asStateFlow()

    fun validateFirstName(value: String): String? {
        if (value.isBlank()) return "El nombre es requerido"
        if (value.length < 2) return "Minimo 2 caracteres"
        if (!value.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
        return null
    }

    fun validateLastName(value: String): String? {
        if (value.isBlank()) return "Los apellidos son requeridos"
        if (value.length < 2) return "Minimo 2 caracteres"
        if (!value.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
        return null
    }

    fun validateUsername(value: String): String? {
        if (value.isBlank()) return "El usuario es requerido"
        if (value.length < 4) return "Minimo 4 caracteres"
        if (!value.matches(Regex("^[a-zA-Z0-9_.]+$"))) return "Solo letras, numeros, _ y ."
        return null
    }

    fun validatePhone(value: String): String? {
        if (value.isBlank()) return "El telefono es requerido"
        if (!value.all { it.isDigit() }) return "Solo numeros"
        if (value.length !in 10..15) return "Entre 10 y 15 digitos"
        return null
    }

    fun validateBirthDate(value: String): String? {
        if (value.isBlank()) return "Selecciona tu fecha de nacimiento"
        return null
    }

    fun isFormValid(
        firstName: String,
        lastName: String,
        username: String,
        phone: String,
        birthDate: String
    ): Boolean {
        return validateFirstName(firstName) == null &&
            validateLastName(lastName) == null &&
            validateUsername(username) == null &&
            validatePhone(phone) == null &&
            validateBirthDate(birthDate) == null
    }

    fun saveProfile(
        uid: String,
        firstName: String,
        lastName: String,
        username: String,
        phone: String,
        birthDate: String
    ) {
        viewModelScope.launch {
            _saveState.value = ResponseService.Loading
            _saveState.value = repository.saveUserInfo(
                UserProfile(
                    id = uid,
                    firstName = firstName,
                    lastName = lastName,
                    userName = username,
                    phone = phone,
                    birthDate = birthDate
                )
            )
        }
    }
}
