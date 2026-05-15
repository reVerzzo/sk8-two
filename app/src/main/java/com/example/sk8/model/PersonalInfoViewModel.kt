package com.example.sk8.model

import com.example.sk8.core.ResponseService
import com.example.sk8.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PersonalInfoViewModel {
    private val repository  = UserRepository();
    private val _saveState = MutableStateFlow<ResponseService<Unit>?>(null)
    val saveState: State
}