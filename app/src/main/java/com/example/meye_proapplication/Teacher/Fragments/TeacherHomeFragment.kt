package com.example.meye_proapplication.Teacher.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meye_proapplication.FastAPI.APIServices.TeacherApiService
import com.example.meye_proapplication.R
import com.example.meye_proapplication.Teacher.Adapters.ScheduleAdapter
import com.example.meye_proapplication.databinding.FragmentTeacherHomeBinding
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeacherHomeFragment : Fragment() {
    private val binding: FragmentTeacherHomeBinding by lazy {
        FragmentTeacherHomeBinding.inflate(layoutInflater)
    }
    private val sharedPrefences by lazy {
        requireContext().getSharedPreferences("User-Session", Context.MODE_PRIVATE)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val teacher_id=sharedPrefences.getString("user_id","No Session Occur").toString()
        fetchSchdule(teacher_id)
    }
    fun fetchSchdule(teacherId:String){
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val api= RetrofitClient.retrofit.create(TeacherApiService::class.java)
                val response=api.getTeacherSchedule(teacherId)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val classes=response.body()?.Lectures?:emptyList()
                        if(classes.isEmpty()){
                            binding.tvNoClasses.visibility= View.VISIBLE
                        }else{
                            binding.rvSchedule.layoutManager= LinearLayoutManager(requireContext())
                            val adapter= ScheduleAdapter(classes)
                            binding.rvSchedule.adapter=adapter
                        }
                    }else{
                        val error=response.errorBody()?.string()
                        Toast.makeText(requireContext(),"Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (ex:Exception){
            Log.e("APIError", "err: ${ex.localizedMessage}")
        }
    }
}