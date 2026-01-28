package com.example.meye_proapplication.Admin.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meye_proapplication.Admin.Adapters.TeacherAdapter
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentHomeBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.AdminApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
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
        fetchTeachers()
    }
    private fun fetchTeachers(){
        try{
            lifecycleScope.launch(Dispatchers.IO) {
                val api= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=api.getAllTeachers()
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val teachersList=response.body()?.teachers?:emptyList()
                        binding.rvStaffList.layoutManager= LinearLayoutManager(requireContext())
                        val adapter= TeacherAdapter(teachersList)
                        binding.rvStaffList.adapter=adapter
                    }
                    else
                        Toast.makeText(requireContext(), "No teachers found", Toast.LENGTH_SHORT).show()
                }
            }
        }catch ( ex: Exception){
                Log.e("API_ERROR", ex.toString())
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }
}