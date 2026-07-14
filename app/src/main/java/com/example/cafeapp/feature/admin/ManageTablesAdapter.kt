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
import com.example.cafeapp.data.remote.dto.TableResponse

class ManageTablesAdapter(
    private val onEditClicked: (TableResponse) -> Unit,
    private val onDeleteClicked: (TableResponse) -> Unit
) : ListAdapter<TableResponse, ManageTablesAdapter.TableViewHolder>(TableDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view, onEditClicked, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TableViewHolder(
        itemView: View,
        private val onEditClicked: (TableResponse) -> Unit,
        private val onDeleteClicked: (TableResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        private val tvArea: TextView = itemView.findViewById(R.id.tvArea)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(table: TableResponse) {
            tvTableNumber.text = "Table ${table.tableNumber}"
            tvArea.text = "Area: ${table.area}"

            btnEdit.setOnClickListener { onEditClicked(table) }
            btnDelete.setOnClickListener { onDeleteClicked(table) }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<TableResponse>() {
        override fun areItemsTheSame(oldItem: TableResponse, newItem: TableResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TableResponse, newItem: TableResponse) = oldItem == newItem
    }
}