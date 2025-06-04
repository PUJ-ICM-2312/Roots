package com.example.roots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository // Necesitas esta clase
import com.example.roots.service.InmuebleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SwipeViewModel(
    private val inmuebleService: InmuebleService,
    sharedFilterViewModel: SharedFilterViewModel // Se usa para observar cambios en los filtros
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Inmueble>>(emptyList())
    val properties: StateFlow<List<Inmueble>> = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    init {
        sharedFilterViewModel.filterState
            .onEach { filters -> // Se ejecuta cada vez que los filtros cambian
                _isLoading.value = true
                _currentIndex.value = 0 // Resetea el índice al aplicar nuevos filtros
                inmuebleService.getFilteredInmuebles(filters) { fetchedProperties ->
                    _properties.value = fetchedProperties
                    _isLoading.value = false
                }
            }
            .launchIn(viewModelScope) // Lanza la recolección en el scope del ViewModel
    }

    fun nextProperty() {
        val currentListSize = _properties.value.size
        if (currentListSize == 0) return

        if (_currentIndex.value < currentListSize - 1) {
            _currentIndex.value++
        } else {
            // Si es el último, indica que no hay más para la UI actual
            // (currentIndex se vuelve igual a currentListSize)
            _currentIndex.value = currentListSize
        }
    }

    fun userLikedProperty(propertyId: String) {
        // Lógica para "Me gusta" (ej. actualizar en Firestore, etc.)
        // Podrías llamar a un método en InmuebleService
        println("User liked property: $propertyId")
        nextProperty()
    }

    fun userDislikedProperty(propertyId: String) {
        // Lógica para "No me gusta"
        println("User disliked property: $propertyId")
        nextProperty()
    }


    // Fábrica para crear SwipeViewModel con dependencias (si no usas Hilt)
    companion object {
        fun provideFactory(
            inmuebleRepository: InmuebleRepository, // O la instancia del servicio directamente
            sharedFilterViewModel: SharedFilterViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SwipeViewModel::class.java)) {
                    val service = InmuebleService(inmuebleRepository) // Crea el servicio aquí
                    return SwipeViewModel(service, sharedFilterViewModel) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}