package com.erdemer.cryptoticker.ui.favorite

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.databinding.FragmentFavoriteBinding
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.ui.home.HomeFragmentDirections
import com.erdemer.cryptoticker.ui.home.HomeViewModel
import com.erdemer.cryptoticker.ui.home.adapter.CoinAdapter
import com.erdemer.cryptoticker.util.ext.collectFlow
import com.erdemer.cryptoticker.util.ext.collectLatestFlow
import com.erdemer.cryptoticker.util.ext.gone
import com.erdemer.cryptoticker.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {
    private val viewModel: FavoriteViewModel by viewModels()
    private var coinsAdapter: CoinAdapter? = null
    override fun onCreateFinished() {
        viewModel.getFavoriteCoins()
        collectLatestFlow(viewModel.uiState, stateCollector)
        collectFlow(viewModel.uiEvents, eventsCollector)
    }

    override fun initListeners() {
        super.initListeners()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val eventsCollector: suspend (FavoriteViewModel.Event) -> Unit = { event ->
        when (event) {
            is FavoriteViewModel.Event.Loading -> setProgressBar(event.isLoading)
            is FavoriteViewModel.Event.Error -> showDialog(event.error.toString(), DialogType.ERROR)
        }
    }

    private val stateCollector: suspend (FavoriteViewModel.UiState) -> Unit = { state ->
        setFavoriteCoinAdapter(state.favoriteCoins)
    }

    private fun setFavoriteCoinAdapter(favoriteCoins: List<CoinEntity>?) {
        coinsAdapter = CoinAdapter {
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToCoinDetailFragment(it.id)
            findNavController().navigate(action)
        }
        coinsAdapter?.submitList(favoriteCoins)
        binding.rvFavoriteCoins.adapter = coinsAdapter
    }

    private fun setProgressBar(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visible()
        else
            binding.progressBar.gone()
    }

}