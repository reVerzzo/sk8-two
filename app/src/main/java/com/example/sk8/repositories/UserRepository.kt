package com.example.sk8.repositories

import com.example.sk8.core.ResponseService
import com.example.sk8.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository: UserService {

    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")

    override suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit> = withContext(Dispatchers.IO) {
        try {
            userCollection.document(userProfile.id).set(userProfile).await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo guardar el perfil: ${e.localizedMessage}")
        }
    }

    override suspend fun getUserInfo(uid: String): ResponseService<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val profile = userCollection.document(uid).get().await().toObject(UserProfile::class.java)
            profile?.let { ResponseService.Success(it) }
                ?: ResponseService.Error("No encontramos la informacion del skater")
        } catch (e: Exception) {
            ResponseService.Error("No se pudo cargar el perfil: ${e.localizedMessage}")
        }
    }
}
