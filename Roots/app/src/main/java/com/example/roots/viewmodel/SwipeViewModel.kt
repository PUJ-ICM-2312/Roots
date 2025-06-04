package com.example.roots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.InmuebleService
import com.example.roots.service.UsuarioService
import com.example.roots.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Repositorios y servicios (pueden inyectarse desde tu contenedor de dependencias)
private val inmuebleRepository = InmuebleRepository()
private val usuarioRepository = UsuarioRepository()
private val inmuebleService = InmuebleService(inmuebleRepository)
private val usuarioService = UsuarioService(usuarioRepository)

class SwipeViewModel(
    private val repository: InmuebleRepository,
    private val service: InmuebleService = InmuebleService(repository),
    private val usuarioService: UsuarioService = UsuarioService(UsuarioRepository())
) : ViewModel() {

    // Lista de todos los inmuebles sin filtrar
    private val _properties = MutableStateFlow<List<Inmueble>>(emptyList())
    val properties: StateFlow<List<Inmueble>> = _properties

    // Indicador de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ÍNDICE del inmueble actualmente mostrado en el Swipe
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    // Conjunto de IDs de inmuebles que el usuario YA ha marcado como "Me gusta"
    private val _likedPropertyIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPropertyIds: StateFlow<Set<String>> = _likedPropertyIds

    // Usuario actual (cargado desde Firestore)
    private var currentUser: Usuario? = null

    init {
        loadInmuebles()
        loadUserAndFavorites()
    }

    /**
     * 1) Carga todos los inmuebles (sin filtro).
     */
    private fun loadInmuebles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _properties.value = service.getAllInmuebles()
            } catch (e: Exception) {
                _properties.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 2) Carga el usuario actual desde Firestore y su lista de favoritos para poblar _likedPropertyIds.
     */
    private fun loadUserAndFavorites() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            usuarioService.obtener(uid) { fetchedUser ->
                if (fetchedUser != null) {
                    currentUser = fetchedUser
                    // Extraemos solo los IDs de los inmuebles que ya están en favoritos
                    val favoritesSet = fetchedUser.favoritos.map { it.id }.toSet()
                    _likedPropertyIds.value = favoritesSet
                }
            }
        }
    }

    /**
     * 3) Cuando el usuario da "Me gusta" en un swipe:
     *    - Validamos que ese ID no esté ya en _likedPropertyIds
     *    - Si no está, lo agregamos tanto a:
     *       a) La lista local de favoritos de currentUser
     *       b) Incrementamos el contador numFavoritos en el Inmueble correspondiente
     *       c) Persistimos ambos cambios en Firestore (usuario e inmueble)
     *    - Finalmente avanzamos el índice para mostrar el siguiente inmueble
     */
    fun userLikedProperty(propertyId: String) {
        // Si el usuario ya dio "like" a este inmueble, solo avanzamos el índice
        if (_likedPropertyIds.value.contains(propertyId)) {
            _currentIndex.value = (_currentIndex.value + 1).coerceAtMost(_properties.value.size)
            return
        }

        // 1) Actualizamos localmente el conjunto de liked IDs
        _likedPropertyIds.value = _likedPropertyIds.value + propertyId

        // 2) Incrementamos el numFavoritos en el Inmueble dentro de _properties
        val updatedList = _properties.value.map { inmueble ->
            if (inmueble.id == propertyId) {
                inmueble.copy(numFavoritos = inmueble.numFavoritos + 1)
            } else {
                inmueble
            }
        }
        _properties.value = updatedList

        // 3) Persistimos el cambio de numFavoritos en Firestore
        val inmuebleToUpdate = updatedList.firstOrNull { it.id == propertyId }
        inmuebleToUpdate?.let { service.actualizar(it) { /* opcional: chequear éxito/fracaso */ } }

        // 4) Agregamos el inmueble a la lista de favoritos del usuario y persistimos
        currentUser?.let { user ->
            // Creamos una nueva lista mutable de favoritos
            val nuevosFavoritos = user.favoritos.toMutableList().apply {
                // Asumimos que el Inmueble ya contiene todos los datos necesarios
                inmuebleToUpdate?.let { add(it) }
            }
            val usuarioActualizado = user.copy(favoritos = nuevosFavoritos)
            // Persistimos el usuario actualizado
            usuarioService.actualizar(usuarioActualizado) { success ->
                if (success) {
                    currentUser = usuarioActualizado
                } else {
                    // Si falla la persistencia, revertimos cambios locales
                    _likedPropertyIds.value = _likedPropertyIds.value - propertyId
                    val revertedList = _properties.value.map { inmueble ->
                        if (inmueble.id == propertyId) {
                            inmueble.copy(numFavoritos = inmueble.numFavoritos - 1)
                        } else {
                            inmueble
                        }
                    }
                    _properties.value = revertedList
                }
            }
        }

        // 5) Finalmente avanzamos el índice (sin sobrepasar el tamaño)
        _currentIndex.value = (_currentIndex.value + 1).coerceAtMost(_properties.value.size)
    }

    /**
     * 4) Cuando el usuario da "No me gusta":
     *    - Simplemente avanzamos el índice. No hacemos nada con favoritos.
     */
    fun userDislikedProperty(propertyId: String) {
        _currentIndex.value = (_currentIndex.value + 1).coerceAtMost(_properties.value.size)
    }

    companion object {
        fun provideFactory(
            repository: InmuebleRepository,
            usuarioService: UsuarioService
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SwipeViewModel(repository, InmuebleService(repository), usuarioService) as T
            }
        }
    }
}
