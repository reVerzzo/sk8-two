package com.example.sk8.model

data class Spot(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdBy: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
