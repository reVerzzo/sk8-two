package com.example.sk8.core
import com.google.firebase.auth.FirebaseUser

interface Authentication {
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestSignUp(email: String, password: String): ResponseService<FirebaseUser>
}