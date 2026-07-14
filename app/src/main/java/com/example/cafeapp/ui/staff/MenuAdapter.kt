package com.example.cafeapp.ui.staff

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.data.local.entity.MenuEntity

class MenuAdapter(private val onAddClicked: (MenuEntity) -> Unit) :
    ListAdapter<MenuEntity, MenuAdapter.MenuViewHolder>(MenuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view, onAddClicked) // Pass it to the ViewHolder
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = getItem(position)
        holder.bind(menu)
    }

    class MenuViewHolder(itemView: View, private val onAddClicked: (MenuEntity) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvMenuName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvMenuCategory)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvMenuPrice)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAddToOrder) // Find the button

        fun bind(menu: MenuEntity) {
            tvName.text = menu.name
            tvCategory.text = menu.category
            tvPrice.text = "Rp ${menu.price.toInt()}"

            // ⚠️ THE TRIGGER: Did you forget this line?
            btnAdd.setOnClickListener {
                Log.d("CafeCart", "0. Adapter button clicked!")
                onAddClicked(menu)
            }
        }
    }

    class MenuDiffCallback : DiffUtil.ItemCallback<MenuEntity>() {
        override fun areItemsTheSame(oldItem: MenuEntity, newItem: MenuEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MenuEntity, newItem: MenuEntity) = oldItem == newItem
    }
}