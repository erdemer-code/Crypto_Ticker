package com.erdemer.cryptoticker.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemer.cryptoticker.data.local.CoinDao
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.util.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val coinDao: CoinDao
): ViewModel() {
    private val _uiEvents = Channel<Event>(Channel.UNLIMITED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val db = Firebase.firestore

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun getFavoriteCoins() {
        viewModelScope.launch {
            _uiEvents.send(Event.Loading(true))
            getDataFromFireStore()
            _uiEvents.send(Event.Loading(false))
        }
    }

    private fun getDataFromFireStore() {
        db.collection(Constants.DB_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                val items = mutableListOf<CoinEntity>()
                for (document in result) {
                    val coin = document.toObject<CoinEntity>()
                    items.add(coin)
                }
                _uiState.update { oldState ->
                    oldState.copy(
                        favoriteCoins = items
                    )
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _uiEvents.send(Event.Error(exception.message))
                }
            }
    }


    data class UiState(
        val favoriteCoins: List<CoinEntity>? = null
    )

    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }
}