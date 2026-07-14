package com.example.cafeapp.feature.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.data.local.entity.DraftCartEntity

class CartDetailAdapter(
    private val onQuantityChange: (DraftCartEntity, Boolean) -> Unit,
    private val onCustomizationSave: (DraftCartEntity, String) -> Unit,
    private val onRemove: (DraftCartEntity) -> Unit
) : ListAdapter<DraftCartEntity, CartDetailAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_detail, parent, false)
        return CartViewHolder(view, onQuantityChange, onCustomizationSave, onRemove)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        itemView: View,
        private val onQuantityChange: (DraftCartEntity, Boolean) -> Unit,
        private val onCustomizationSave: (DraftCartEntity, String) -> Unit,
        private val onRemove: (DraftCartEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        private val etCustomization: EditText = itemView.findViewById(R.id.etCustomization)
        private val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        private val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        private val btnRemove: Button = itemView.findViewById(R.id.btnRemove)

        fun bind(item: DraftCartEntity) {
            tvName.text = item.name
            tvPrice.text = "Rp ${(item.price * item.quantity).toInt()}"
            tvQuantity.text = item.quantity.toString()

            etCustomization.onFocusChangeListener = null
            etCustomization.setText(item.customization)

            etCustomization.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    onCustomizationSave(item, etCustomization.text.toString())
                }
            }

            btnPlus.setOnClickListener { onQuantityChange(item, true) }
            btnMinus.setOnClickListener { onQuantityChange(item, false) }
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<DraftCartEntity>() {
        override fun areItemsTheSame(oldItem: DraftCartEntity, newItem: DraftCartEntity) = oldItem.cartId == newItem.cartId
        override fun areContentsTheSame(oldItem: DraftCartEntity, newItem: DraftCartEntity) = oldItem == newItem
    }
}