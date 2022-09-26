package com.erdemer.cryptoticker.ui.coinDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemer.cryptoticker.data.local.CoinDao
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.repository.CoinDetailRepository
import com.erdemer.cryptoticker.ui.coinDetail.manager.CoinDetailRequestServiceManager
import com.erdemer.cryptoticker.ui.coinDetail.mapper.toCoinDetailUiModel
import com.erdemer.cryptoticker.ui.coinDetail.model.CoinDetailUiModel
import com.erdemer.cryptoticker.util.Constants
import com.erdemer.cryptoticker.util.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinDetailRepository: CoinDetailRepository,
    private val coinDao: CoinDao,
    private val coinDetailRequestServiceManager: CoinDetailRequestServiceManager
) : ViewModel() {

    private val _uiEvents = Channel<Event>(Channel.UNLIMITED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            coinDetailRequestServiceManager.state.collect {
                if (it.coinDetailResponse != null) {
                    delay(1000L)
                    _uiEvents.send(Event.Loading(true))
                    _uiState.update { oldState ->
                        oldState.copy(
                            currentState = State.COIN_DETAIL_API,
                            coinDetail = it.coinDetailResponse.toCoinDetailUiModel()
                        )
                    }
                    _uiEvents.send(Event.Loading(false))

                }
            }
        }
    }
    private val db = Firebase.firestore
    fun getCoinDetail(coinId: String) {
        viewModelScope.launch {
            _uiEvents.send(Event.Loading(true))
            when (val response = coinDetailRepository.getCoinDetail(coinId)) {
                is Resource.Success -> {
                    _uiState.update { oldState ->
                        oldState.copy(
                            currentState = State.COIN_DETAIL_API,
                            coinDetail = response.data?.toCoinDetailUiModel()
                        )
                    }
                }
                is Resource.Error -> {
                    _uiEvents.send(Event.Error(response.message))
                }
            }
            _uiEvents.send(Event.Loading(false))
        }
    }

    fun getSelectedCoin(coinId: String) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    selectedCoinEntity = coinDao.getCoin(coinId)
                )
            }
        }
    }

    fun updateChangeInterval(coinEntity: CoinEntity) {
        viewModelScope.launch {
            coinDao.updateCoin(coinEntity)
            _uiState.update { oldState ->
                oldState.copy(
                    changeIntervalArrayPosition = coinEntity.priceChangeIntervalPosition
                )
            }
        }
    }

    fun updateCoinFavorite(coinEntity: CoinEntity) {
        val coin = hashMapOf(
            Constants.id to coinEntity.id,
            Constants.name to coinEntity.name,
            Constants.symbol to coinEntity.symbol,
            Constants.isFavorite to coinEntity.isFavorite,
            Constants.priceChangeIntervalPosition to coinEntity.priceChangeIntervalPosition,
        )
        if (uiState.value.isFavorite && coinEntity.priceChangeIntervalPosition != -1) {
            db.collection(Constants.DB_COLLECTION_NAME)
                .document(uiState.value.documentId ?: "")
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                }.addOnFailureListener {
                    Log.d("Firestore", "DocumentSnapshot failed to delete!")
                }
            _uiState.update { oldState ->
                oldState.copy(
                    currentState = State.UPDATE_COIN_DETAIL,
                    isFavorite = false,
                    changeIntervalArrayPosition = -1
                )
            }
        } else {
            db.collection(Constants.DB_COLLECTION_NAME).add(coin).addOnSuccessListener {
                Log.d("CoinDetail", "DocumentSnapshot added with ID: ${it.id}")
                _uiState.update { oldState ->
                    oldState.copy(
                        currentState = State.UPDATE_COIN_DETAIL,
                        documentId = it.id,
                        isFavorite = true,
                        changeIntervalArrayPosition = coinEntity.priceChangeIntervalPosition
                    )
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _uiEvents.send(Event.Error(it.message))
                }
            }
        }
    }

    fun getCoinDataFromFirestore(id: String) {
        db.collection(Constants.DB_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document[Constants.id] == id) {
                        _uiState.update { oldState ->
                            oldState.copy(
                                currentState = State.UPDATE_COIN_DETAIL,
                                isFavorite = document[Constants.isFavorite] as Boolean,
                                documentId = document.id
                            )
                        }
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _uiEvents.send(Event.Error(exception.message))
                }
            }

    }

    enum class State {
        Initial,
        COIN_DETAIL_API,
        UPDATE_COIN_DETAIL
    }


    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }

    data class UiState(
        val coinDetail: CoinDetailUiModel? = null,
        val currentState: State = State.Initial,
        val isFavorite: Boolean = false,
        val documentId: String? = null,
        val changeIntervalArrayPosition: Int? = null,
        val selectedCoinEntity: CoinEntity? = null
    )

}