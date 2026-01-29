package com.example.meye_proapplication.Admin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meye_proapplication.Admin.Adapters.CameraAdapter
import com.example.meye_proapplication.FastAPI.APIModels.VenueCameraGroup
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentCameraListBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.AdminApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraListFragment : Fragment() {
    private val binding: FragmentCameraListBinding by lazy{
        FragmentCameraListBinding.inflate(layoutInflater)
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
        val dvrMac = arguments?.getString("MAC") ?: ""
        val dvrName = arguments?.getString("Name") ?: "DVR Details"
        binding.tvDvrName.text="$dvrName\n$dvrMac"
        fetchConnectedCameras(dvrMac)
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun fetchConnectedCameras(mac: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response = api.getCameras(mac) // API Call

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val rawList = response.body()?.cameras ?: emptyList()

                        // --- 🧠 MAGIC LOGIC: Grouping Data ---
                        // Hum ek Map banayenge jahan Key = Venue Name hogi
                        val groupedMap = mutableMapOf<String, VenueCameraGroup>()

                        for (cam in rawList) {
                            // 1. Agar ye Venue pehli baar aya hai, to naya object banao
                            if (!groupedMap.containsKey(cam.venue)) {
                                groupedMap[cam.venue] = VenueCameraGroup(cam.venue)
                            }

                            // 2. Map se wo object nikalo
                            val currentGroup = groupedMap[cam.venue]!!

                            // 3. Check karo ye Front hai ya Back aur data fill karo
                            if (cam.viewType.equals("Front", ignoreCase = true)) {
                                currentGroup.frontChannel = cam.channel
                                currentGroup.frontStatus = cam.status
                            } else {
                                // Back / Rear logic
                                currentGroup.backChannel = cam.channel
                                currentGroup.backStatus = cam.status
                            }
                        }

                        // Map ki values ko List bana kar Adapter ko dedo
                        val finalGroupedList = groupedMap.values.toList()

                        // Adapter initialize aur update karein
                        if (finalGroupedList.isNotEmpty()) {
                            val adapter = CameraAdapter(finalGroupedList)
                            binding.rvCameraList.layoutManager =
                                LinearLayoutManager(requireContext())
                            binding.rvCameraList.adapter = adapter

                        } else {
                            Toast.makeText(requireContext(), "No Cameras Found", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}