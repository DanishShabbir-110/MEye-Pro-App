package com.example.meye_proapplication.Teacher.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meye_proapplication.FastAPI.APIModels.Lecture
import com.example.meye_proapplication.databinding.RowDvrBinding
import com.example.meye_proapplication.databinding.RowTeacherScheduleBinding

class ScheduleAdapter(val classesList: List<Lecture>): RecyclerView.Adapter<ScheduleAdapter.VH>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val view= RowTeacherScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return VH(view)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {
        val lecture=classesList[position]
        holder.binding.tvCourseName.text="Course: ${lecture.Course_Name}"
        holder.binding.tvDiscipline.text="Discipline: ${lecture.Discipline}"
        holder.binding.tvVenue.text=lecture.Venue
        holder.binding.tvTime.text="Time: ${lecture.Class_Start_time}-${lecture.Class_End_time}"
    }

    override fun getItemCount(): Int =classesList.size

    class VH(val binding: RowTeacherScheduleBinding): RecyclerView.ViewHolder(binding.root)
}