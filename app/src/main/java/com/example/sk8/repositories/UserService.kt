package com.example.sk8.repositories

import com.example.sk8.core.ResponseService
import com.example.sk8.model.UserProfile

interface UserService {
    suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit>
    suspend fun getUserInfo(uid: String): ResponseService<UserProfile>
}
