package com.example.meye_proapplication.Admin.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meye_proapplication.FastAPI.APIModels.ShowDVR
import com.example.meye_proapplication.databinding.RowDvrBinding

class DVRAdapter(var DVR_list:List<ShowDVR>,val onDvrClicked:(ShowDVR)->Unit): RecyclerView.Adapter<DVRAdapter.VH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val view= RowDvrBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return VH(view)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {
        val dvr=DVR_list[position]
        holder.binding.tvDvrName.text="Name: ${dvr.name}"
        holder.binding.tvDvrMAC.text="MAC: ${dvr.mac}"
        holder.binding.ivArrow.setOnClickListener {
            onDvrClicked(dvr)
        }
        holder.itemView.setOnClickListener {
            onDvrClicked(dvr)
        }
    }

    override fun getItemCount(): Int=DVR_list.size

    class VH(val binding: RowDvrBinding): RecyclerView.ViewHolder(binding.root)
}