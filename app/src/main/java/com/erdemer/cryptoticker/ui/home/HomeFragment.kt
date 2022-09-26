package com.erdemer.cryptoticker.ui.home

import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentHomeBinding
import com.erdemer.cryptoticker.model.local.CoinEntity
import com.erdemer.cryptoticker.ui.BaseFragment
import com.erdemer.cryptoticker.ui.home.adapter.CoinAdapter
import com.erdemer.cryptoticker.util.ext.collectFlow
import com.erdemer.cryptoticker.util.ext.collectLatestFlow
import com.erdemer.cryptoticker.util.ext.gone
import com.erdemer.cryptoticker.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()
    private var coinsAdapter: CoinAdapter? = null
    override fun onCreateFinished() {
        collectLatestFlow(viewModel.uiState, stateCollector)
        collectFlow(viewModel.uiEvents, eventsCollector)

    }

    override fun initListeners() {
        super.initListeners()
        binding.toolbarHome.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                R.id.favorite -> {
                    findNavController().navigate(R.id.action_homeFragment_to_favoriteFragment)
                    true
                }
                else -> false
            }

        }
        val searchView = binding.toolbarHome.menu.findItem(R.id.search).actionView as SearchView
        val textId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = searchView.findViewById<TextView>(textId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.filterCoinsList(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty().not()) {
                    if (newText!!.length >= 3)
                        viewModel.filterCoinsList(newText)
                }
                return false
            }
        })
    }

    private val stateCollector: suspend (HomeViewModel.UiState) -> Unit = { state ->
        when(state.currentState){
            HomeViewModel.State.Initial -> viewModel.getCoinsList()
            HomeViewModel.State.AllCoins -> setCoinsListAdapter(state.coinsList)
            HomeViewModel.State.FilterCoins -> setCoinsListAdapter(state.filteredCoins)
        }
    }
    private val eventsCollector: suspend (HomeViewModel.Event) -> Unit = { event ->
        when (event) {
            is HomeViewModel.Event.Loading -> setProgressBar(event.isLoading)
            is HomeViewModel.Event.Error -> showDialog(event.error.toString(), DialogType.ERROR)
        }
    }

    private fun setProgressBar(isLoading: Boolean) {
        if (isLoading)
            binding.progressBarHome.visible()
        else
            binding.progressBarHome.gone()
    }

    private fun setCoinsListAdapter(coinsList: List<CoinEntity>?) {
        coinsAdapter = CoinAdapter {
            val action = HomeFragmentDirections.actionHomeFragmentToCoinDetailFragment(it.id)
            findNavController().navigate(action)
        }
        coinsAdapter?.submitList(coinsList)
        binding.rvCoins.adapter = coinsAdapter
    }
}