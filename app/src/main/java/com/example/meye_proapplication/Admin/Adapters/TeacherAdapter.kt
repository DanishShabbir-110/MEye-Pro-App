package com.example.meye_proapplication.Admin.Adapters

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.meye_proapplication.FastAPI.APIModels.ShowTeacher
import com.example.meye_proapplication.databinding.RowStaffMemberBinding

class TeacherAdapter(val teachers:List<ShowTeacher>): RecyclerView.Adapter<TeacherAdapter.VH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val view= RowStaffMemberBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return VH(view)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {
        val teacher=teachers[position]

        holder.binding.tvStaffName.text=teacher.name.toString()
        val path="http://10.97.174.61:8000/${teacher.pic}"
        Glide.with(holder.itemView.context)
            .load(path)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Purani cache delete karega
            .skipMemoryCache(true) // Memory mein save nahi karega
            .into(holder.binding.ivProfile)
    }

    override fun getItemCount(): Int=teachers.size

    class VH(val binding: RowStaffMemberBinding) : RecyclerView.ViewHolder(binding.root)
}