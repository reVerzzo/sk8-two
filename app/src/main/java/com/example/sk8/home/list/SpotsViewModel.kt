package com.example.sk8.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sk8.core.ResponseService
import com.example.sk8.model.Spot
import com.example.sk8.repositories.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpotsViewModel(
    private val repository: SpotRepository = SpotRepository()
) : ViewModel() {
    private val _spotsState = MutableStateFlow<ResponseService<List<Spot>>?>(null)
    val spotsState: StateFlow<ResponseService<List<Spot>>?> = _spotsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResponseService<Unit>?>(null)
    val actionState: StateFlow<ResponseService<Unit>?> = _actionState.asStateFlow()

    fun loadSpots(userId: String) {
        viewModelScope.launch {
            _spotsState.value = ResponseService.Loading
            _spotsState.value = repository.getSpots(userId)
        }
    }

    fun deleteSpot(spot: Spot, userId: String) {
        viewModelScope.launch {
            _actionState.value = ResponseService.Loading
            _actionState.value = repository.deleteSpot(spot.id)
            loadSpots(userId)
        }
    }
}
