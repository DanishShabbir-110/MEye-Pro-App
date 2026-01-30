package com.example.meye_proapplication.Admin.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meye_proapplication.FastAPI.APIModels.VenueCameraGroup
import com.example.meye_proapplication.databinding.RowCameraBinding

class CameraAdapter(val groupList:List<VenueCameraGroup>): RecyclerView.Adapter<CameraAdapter.VH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val view= RowCameraBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(view)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {
        val item = groupList[position]

        // 1. Venue Name Set karein
        holder.binding.tvVenueName.text = "Venue: ${item.venueName}"

        // --- FRONT CAMERA LOGIC ---
        if (item.frontChannel != null) {
            // Data show karein
            holder.binding.tvFrontChannel.text = item.frontChannel.toString()
            holder.binding.tvFrontStatus.visibility = View.VISIBLE
            holder.binding.tvFrontStatus.text = item.frontStatus ?: "N/A"

            // Color Change Logic (Green/Red)
            if (item.frontStatus == "Online") {
                holder.binding.tvFrontStatus.setTextColor(Color.parseColor("#2E7D32")) // Green
                holder.binding.tvFrontStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
            } else {
                holder.binding.tvFrontStatus.setTextColor(Color.RED)
                holder.binding.tvFrontStatus.setBackgroundColor(Color.parseColor("#FFEBEE"))
            }
        } else {
            holder.binding.tvFrontChannel.text = "-"
            holder.binding.tvFrontStatus.visibility = View.GONE
        }

        // --- BACK CAMERA LOGIC ---
        if (item.backChannel != null) {
            holder.binding.tvBackChannel.text = item.backChannel.toString()
            holder.binding.tvBackStatus.visibility = View.VISIBLE
            holder.binding.tvBackStatus.text = item.backStatus ?: "N/A"

            // Color Change Logic
            if (item.backStatus == "Online") {
                holder.binding.tvBackStatus.setTextColor(Color.parseColor("#2E7D32")) // Green
                holder.binding.tvBackStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
            } else {
                holder.binding.tvBackStatus.setTextColor(Color.RED)
                holder.binding.tvBackStatus.setBackgroundColor(Color.parseColor("#FFEBEE"))
            }
        } else {
            // Agar Back Camera nahi hai
            holder.binding.tvBackChannel.text = "-"
            holder.binding.tvBackStatus.visibility = View.GONE
        }

        // Click Listeners (Future ke liye)
        holder.binding.ivEdit.setOnClickListener {
            // Edit ka code baad mein ayega
        }
        holder.binding.ivDelete.setOnClickListener {
            // Delete ka code baad mein ayega
        }
    }

    override fun getItemCount(): Int=groupList.size
    class VH(val binding: RowCameraBinding): RecyclerView.ViewHolder(binding.root)
}