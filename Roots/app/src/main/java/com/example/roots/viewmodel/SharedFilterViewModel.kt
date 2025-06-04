package com.example.roots.viewmodel

import androidx.lifecycle.ViewModel
import com.example.roots.screens.FilterState // Asegúrate que la ruta de importación sea correcta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado inicial de los filtros (puedes moverlo aquí si lo prefieres)
val initialFilterState = FilterState(
    selectedEstado = "Todos",
    ocultarVistos = false,
    selectedTipoOferta = "Venta",
    selectedTipoInmueble = emptyList(),
    selectedHabitaciones = emptyList(),
    selectedBanios = emptyList(),
    selectedEstratos = emptyList(),
    selectedAntiguedad = "Todos",
    selectedFecha = "Hoy", // O "Todos" si quieres que no haya filtro de fecha por defecto
    parqueadero = false,
    precioMin = "",
    precioMax = "",
    areaMin = "",
    areaMax = ""
)

class SharedFilterViewModel : ViewModel() {
    private val _filterState = MutableStateFlow(initialFilterState)
    val filterState = _filterState.asStateFlow()

    fun updateFilters(newFilters: FilterState) {
        _filterState.value = newFilters.copy(
            // Asegura que las listas sean nuevas instancias si es necesario, aunque FilterState ahora usa List inmutables
            selectedTipoInmueble = newFilters.selectedTipoInmueble.toList(),
            selectedHabitaciones = newFilters.selectedHabitaciones.toList(),
            selectedBanios = newFilters.selectedBanios.toList(),
            selectedEstratos = newFilters.selectedEstratos.toList()
        )
    }

    fun clearFilters() {
        _filterState.value = initialFilterState
    }
}