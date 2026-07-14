package com.example.cafeapp.feature.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.data.remote.dto.TableResponse
import com.google.android.material.switchmaterial.SwitchMaterial

class StaffTableAdapter(private val onStatusChanged: (String, Boolean) -> Unit) :
    ListAdapter<TableResponse, StaffTableAdapter.TableViewHolder>(TableDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff_table, parent, false)
        return TableViewHolder(view, onStatusChanged)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TableViewHolder(itemView: View, private val onStatusChanged: (String, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        private val tvArea: TextView = itemView.findViewById(R.id.tvArea)
        private val switchAvailability: SwitchMaterial = itemView.findViewById(R.id.switchAvailability)

        fun bind(table: TableResponse) {
            tvTableNumber.text = "Table ${table.tableNumber}"
            tvArea.text = "Seats: ${table.seatCount} | ${table.area}"

            switchAvailability.setOnCheckedChangeListener(null)

            val isInitiallyAvailable = table.status == "Available"
            switchAvailability.isChecked = isInitiallyAvailable
            switchAvailability.text = if (isInitiallyAvailable) "Available" else "Unavailable"

            switchAvailability.setOnCheckedChangeListener { _, isChecked ->
                switchAvailability.text = if (isChecked) "Available" else "Unavailable"

                onStatusChanged(table.id, isChecked)
            }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<TableResponse>() {
        override fun areItemsTheSame(oldItem: TableResponse, newItem: TableResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TableResponse, newItem: TableResponse) = oldItem == newItem
    }
}