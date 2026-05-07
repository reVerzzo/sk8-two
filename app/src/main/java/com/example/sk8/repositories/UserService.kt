package com.example.sk8.repositories

import com.example.sk8.core.ResponseService
import com.example.sk8.model.UserProfile

interface UserService {
    suspend fun saveUser(UserProfile: UserProfile): ResponseService<Unit>
}