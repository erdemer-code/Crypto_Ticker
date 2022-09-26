package com.erdemer.cryptoticker.ui.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemer.cryptoticker.data.local.CoinDao
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.model.response.CoinsListResponse
import com.erdemer.cryptoticker.repository.CoinsListRepository
import com.erdemer.cryptoticker.ui.home.mapper.toCoinEntity
import com.erdemer.cryptoticker.util.Constants
import com.erdemer.cryptoticker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val coinsListRepository: CoinsListRepository,
    private val coinDao: CoinDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _uiEvents = Channel<Event>(Channel.UNLIMITED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun getCoinsList(){
        viewModelScope.launch {
            _uiEvents.send(Event.Loading(true))
            when(val response = coinsListRepository.getCoinsList()){
                is Resource.Success -> {
                    if (!sharedPreferences.getBoolean(Constants.APP_OPENED_BEFORE,false)) {
                        _uiState.update { oldState ->
                            oldState.copy(
                                coinsList = response.data?.toCoinEntity(),
                                currentState = State.AllCoins
                            )
                        }
                        saveRoom(response.data)
                    } else
                        getAllDataFromRoom()
                }
                is Resource.Error -> {
                    _uiEvents.send(Event.Error(response.message))
                }
            }
            _uiEvents.send(Event.Loading(false))
        }
    }

    private fun saveRoom(data: List<CoinsListResponse>?) {
        viewModelScope.launch {
            data?.toCoinEntity()?.toTypedArray()?.let { coinDao.insertAll(*it) }
            sharedPreferences.edit().putBoolean(Constants.APP_OPENED_BEFORE,true).apply()
        }
    }

    fun filterCoinsList(query: String){
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    filteredCoins = coinDao.searchCoins(query),
                    currentState = State.FilterCoins
                )
            }
        }
    }

    private fun getAllDataFromRoom(){
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    coinsList = coinDao.getAllCoins(),
                    currentState = State.AllCoins
                )
            }
        }
    }


    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }
    enum class State {
        Initial,
        AllCoins,
        FilterCoins
    }

    data class UiState(
        val coinsList: List<CoinEntity>? = null,
        val currentState : State = State.Initial,
        val filteredCoins: List<CoinEntity>? = null
    )
}