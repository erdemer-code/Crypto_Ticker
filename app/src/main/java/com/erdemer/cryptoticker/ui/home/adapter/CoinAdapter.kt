package com.erdemer.cryptoticker.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.CoinItemBinding
import com.erdemer.cryptoticker.model.local.CoinEntity

class CoinAdapter(private val onClickListener: (CoinEntity) -> Unit) :
    ListAdapter<CoinEntity, CoinAdapter.CoinViewHolder>(
        DIFF_CALLBACK
    ) {

    inner class CoinViewHolder(val binding: CoinItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoinEntity>() {
            override fun areItemsTheSame(
                oldItem: CoinEntity,
                newItem: CoinEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CoinEntity,
                newItem: CoinEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding: CoinItemBinding = CoinItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        with(holder){
            with(getItem(position)){
                binding.root.setOnClickListener {
                    onClickListener(this)
                }
                binding.tvCoinName.text = binding.root.context.getString(R.string.coin_item_name,name,symbol)
            }
            if (this.adapterPosition < itemCount - 1) {
                binding.divider.visibility = android.view.View.VISIBLE
            } else {
                binding.divider.visibility = android.view.View.GONE
            }

        }
    }
}