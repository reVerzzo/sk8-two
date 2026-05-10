package com.example.sk8.core
sealed class ResponseService {
    data class Success(val value: Boolean)
    data class Error(val error: String)
}