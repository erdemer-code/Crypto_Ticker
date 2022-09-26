package com.erdemer.cryptoticker.ui.coinDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentCoinDetailBinding
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.ui.coinDetail.model.CoinDetailUiModel
import com.erdemer.cryptoticker.util.CoinDetailService
import com.erdemer.cryptoticker.util.ext.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinDetailFragment :
    BaseFragment<FragmentCoinDetailBinding>(FragmentCoinDetailBinding::inflate) {
    private val navArg: CoinDetailFragmentArgs by navArgs()
    private val viewModel: CoinDetailViewModel by viewModels()
    override fun onCreateFinished() {
        navArg.coinId.apply {
            viewModel.getCoinDataFromFirestore(this)
            viewModel.getCoinDetail(this)
            viewModel.getSelectedCoin(this)
        }
        collectLatestFlow(viewModel.uiState, stateCollector)
        collectFlow(viewModel.uiEvents, eventCollector)
    }


    private val stateCollector: suspend (CoinDetailViewModel.UiState) -> Unit = { state ->
        when (state.currentState) {
            CoinDetailViewModel.State.Initial -> onInitState()
            CoinDetailViewModel.State.COIN_DETAIL_API -> onServiceResponseArrived(state.coinDetail)
            CoinDetailViewModel.State.UPDATE_COIN_DETAIL -> updateCoinExecuted(state.isFavorite)
        }
    }

    private fun updateCoinExecuted(favorite: Boolean) {
        setFavoriteIconColor(favorite)
    }

    private fun onInitState() {
        setDropDownMenuAdapter()
    }

    private fun setDropDownMenuAdapter() {
        val refreshIntervals = resources.getStringArray(R.array.refresh_interval)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, refreshIntervals)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        viewModel.uiState.value.selectedCoinEntity?.priceChangeIntervalPosition?.let {
            if (it != -1) {
                binding.autoCompleteTextView.setText(
                    refreshIntervals[viewModel.uiState.value.selectedCoinEntity?.priceChangeIntervalPosition!!],
                    false
                )
            }
        }

    }

    private fun setFavoriteIconColor(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivCoinDetailFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        } else {
            binding.ivCoinDetailFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
    }

    private val eventCollector: suspend (CoinDetailViewModel.Event) -> Unit = { event ->
        when (event) {
            is CoinDetailViewModel.Event.Loading -> setProgressBar(event.isLoading)
            is CoinDetailViewModel.Event.Error -> showDialog(
                event.error ?: "An error occurred!",
                DialogType.ERROR
            )
        }
    }

    private fun setProgressBar(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visible()
        else
            binding.progressBar.gone()
    }

    @SuppressLint("StringFormatInvalid")
    private fun onServiceResponseArrived(coinDetail: CoinDetailUiModel?) {
        coinDetail?.name?.let { binding.tvCoinName.text = getString(R.string.coin_detail_name, it) }
        coinDetail?.symbol?.let {
            binding.tvCoinSymbol.text = getString(R.string.coin_detail_symbol, it)
        }
        coinDetail?.hashingAlgorithm?.let {
            binding.tvCoinHashAlg.text = getString(R.string.coin_detail_hashing_algorithm, it)
        } ?: binding.tvCoinHashAlg.gone()
        coinDetail?.generalPriceChangePercent24h?.let {
            binding.tvCoinPriceChange24h.text = getString(R.string.general_price_change_24h, it)
        }

        coinDetail?.imageUrl?.let { binding.ivCoinIcon.loadImage(it) }
        coinDetail?.description?.let {
            binding.tvCoinDescription.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        } ?: kotlin.run { binding.groupDescription.gone() }
        coinDetail?.currentPriceTRY?.let { binding.tvCurrentPriceTr.text = it.toString() }
            ?: kotlin.run { binding.ivLiraIcon.gone() }
        coinDetail?.currentPriceUSD?.let { binding.tvCurrentPriceDollar.text = it.toString() }
            ?: kotlin.run { binding.ivDollarIcon.gone() }
        coinDetail?.currentPriceEUR?.let { binding.tvCurrentPriceEur.text = it.toString() }
            ?: kotlin.run { binding.ivEuroIcon.gone() }

        coinDetail?.priceChangePercent24hTRY?.let {
            binding.groupPriceChange.visible()
            binding.tvCurrentPriceChangeTr.text = it.toString()
            binding.ivLiraChangeArrow.setPriceChangeIcon(it)
        } ?: kotlin.run { binding.ivLiraIconPriceChange.gone() }
        coinDetail?.priceChangePercent24hUSD?.let {
            binding.groupPriceChange.visible()
            binding.tvCurrentPriceChangeDollar.text = it.toString()
            binding.ivDollarChangeArrow.setPriceChangeIcon(it)
        } ?: kotlin.run { binding.ivDollarIconPriceChange.gone() }
        coinDetail?.priceChangePercent24hEUR?.let {
            binding.groupPriceChange.visible()
            binding.tvCurrentPriceChangeEur.text = it.toString()
            binding.ivEuroChangeArrow.setPriceChangeIcon(it)
        } ?: kotlin.run { binding.ivEuroIconPriceChange.gone() }


    }

    override fun initListeners() {
        super.initListeners()
        binding.ivCoinDetailFavorite.setOnClickListener {
            viewModel.uiState.value.selectedCoinEntity?.let { coinEntity ->
                viewModel.updateCoinFavorite(
                    CoinEntity(
                        coinEntity.id,
                        coinEntity.name,
                        coinEntity.symbol,
                        viewModel.uiState.value.isFavorite
                    )
                )
            }
        }
        binding.autoCompleteTextView.setOnItemClickListener { adapterView, _, i, _ ->
            val interval = adapterView.getItemAtPosition(i).toString()
            val intervalInSeconds = interval.split(" ")[0].toInt()
            val milliSeconds = intervalInSeconds * 1000L
            viewModel.uiState.value.selectedCoinEntity?.let { entity ->
                viewModel.updateChangeInterval(
                    CoinEntity(
                        entity.id,
                        entity.name,
                        entity.symbol,
                        entity.isFavorite,
                        i
                    )
                )
            }
            /*viewModel.setWorkManager(requireActivity(), viewLifecycleOwner, milliSeconds)*/
            requireActivity().startService(Intent(requireContext(), CoinDetailService::class.java).also {
                it.putExtra(CoinDetailService.COIN_ID, viewModel.uiState.value.selectedCoinEntity?.id)
                it.putExtra(CoinDetailService.TIME_INTERVAL, milliSeconds)
            })
        }
    }
}


