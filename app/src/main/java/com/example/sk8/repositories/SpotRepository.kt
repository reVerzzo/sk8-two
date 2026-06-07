package com.example.sk8.repositories

import com.example.sk8.core.ResponseService
import com.example.sk8.model.Spot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SpotRepository {
    private val spotsCollection = FirebaseFirestore.getInstance().collection("spots")

    suspend fun getSpots(userId: String): ResponseService<List<Spot>> = withContext(Dispatchers.IO) {
        try {
            val spots = spotsCollection
                .whereEqualTo("createdBy", userId)
                .get()
                .await()
                .toObjects(Spot::class.java)
                .sortedByDescending { it.createdAt }
            ResponseService.Success(spots)
        } catch (e: Exception) {
            ResponseService.Error("No se pudieron cargar los spots: ${e.localizedMessage}")
        }
    }

    suspend fun createSpot(
        userId: String,
        name: String,
        description: String,
        latitude: Double,
        longitude: Double
    ): ResponseService<Unit> = withContext(Dispatchers.IO) {
        try {
            val document = spotsCollection.document()
            val now = System.currentTimeMillis()
            val spot = Spot(
                id = document.id,
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude,
                createdBy = userId,
                createdAt = now,
                updatedAt = now
            )
            document.set(spot).await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo guardar el spot: ${e.localizedMessage}")
        }
    }

    suspend fun updateSpot(
        spot: Spot,
        name: String,
        description: String
    ): ResponseService<Unit> = withContext(Dispatchers.IO) {
        try {
            spotsCollection.document(spot.id)
                .set(
                    spot.copy(
                        name = name,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                .await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo actualizar el spot: ${e.localizedMessage}")
        }
    }

    suspend fun deleteSpot(spotId: String): ResponseService<Unit> = withContext(Dispatchers.IO) {
        try {
            spotsCollection.document(spotId).delete().await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo eliminar el spot: ${e.localizedMessage}")
        }
    }
}
