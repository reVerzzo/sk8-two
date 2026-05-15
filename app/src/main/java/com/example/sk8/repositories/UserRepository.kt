package com.example.sk8.repositories

import com.example.sk8.core.ResponseService
import com.example.sk8.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.FirestoreGrpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository: UserService {

    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("User")
    override fun saveUser(UserProfile: UserProfile): ResponseService<Unit> = withContext(Dispatchers.IO) {
        try {
            userCollection.document(userProfile.id)
        }

    }

}