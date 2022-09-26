package com.erdemer.cryptoticker.ui.coinDetail.manager

import com.erdemer.cryptoticker.model.response.coinDetail.CoinDetailResponse
import com.erdemer.cryptoticker.repository.CoinDetailRepository
import com.erdemer.cryptoticker.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


class CoinDetailRequestServiceManager @Inject constructor(
    private val coinDetailRepository: CoinDetailRepository
) {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _event = Channel<Event>(Channel.UNLIMITED)
    private val event = _event.receiveAsFlow()

    private var scope: CoroutineScope?  = null

    operator fun invoke(coinId: String){
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.IO)
        scope?.launch {
            when(val response = coinDetailRepository.getCoinDetail(coinId)){
                is Resource.Success -> {
                   _state.update {
                          it.copy(coinDetailResponse = response.data)
                   }
                }
                is Resource.Error -> {
                    _event.send(Event.Error(response.message))
                }
            }
        }
    }
    data class State(
        val coinDetailResponse: CoinDetailResponse? = null,
    )

    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }
}