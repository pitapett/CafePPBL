package com.example.cafeapp.feature.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.data.remote.dto.StockResponse

class StockAdapter(
    private val onEditClicked: (StockResponse) -> Unit,
    private val onDeleteClicked: (StockResponse) -> Unit
) : ListAdapter<StockResponse, StockAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view, onEditClicked, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StockViewHolder(
        itemView: View,
        private val onEditClicked: (StockResponse) -> Unit,
        private val onDeleteClicked: (StockResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvIngredientName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(stock: StockResponse) {
            tvName.text = stock.ingredient_name
            tvAmount.text = "Amount: ${stock.amount}"

            btnEdit.setOnClickListener { onEditClicked(stock) }
            btnDelete.setOnClickListener { onDeleteClicked(stock) }
        }
    }

    class StockDiffCallback : DiffUtil.ItemCallback<StockResponse>() {
        override fun areItemsTheSame(oldItem: StockResponse, newItem: StockResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: StockResponse, newItem: StockResponse) = oldItem == newItem
    }
}