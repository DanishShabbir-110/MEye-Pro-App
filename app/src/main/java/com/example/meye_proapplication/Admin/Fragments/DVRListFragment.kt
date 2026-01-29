package com.example.meye_proapplication.Admin.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meye_proapplication.Admin.Adapters.DVRAdapter
import com.example.meye_proapplication.FastAPI.APIModels.AddDVR
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.DialogAddDvrBinding
import com.example.meye_proapplication.databinding.FragmentDVRListBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.AdminApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DVRListFragment : Fragment() {
    private val sharedPrefences by lazy {
        requireContext().getSharedPreferences("User-Session", Context.MODE_PRIVATE)
    }
    private val binding: FragmentDVRListBinding by lazy {
        FragmentDVRListBinding.inflate(layoutInflater)
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
        fetchDvrs()
        binding.fabAddDvr.setOnClickListener {
            showAddDVRDialog()
        }
    }
    fun fetchDvrs(){
        try {
            lifecycleScope.launch(Dispatchers.IO){
                val api= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=api.getAllDVRS()
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val dvrs=response.body()?.dvrs?:emptyList()
                        binding.rvDvrList.layoutManager= LinearLayoutManager(requireContext())
                        val adapter= DVRAdapter(dvrs){showDVR ->
                            val bundle= Bundle()
                            bundle.putString("MAC",showDVR.mac)
                            bundle.putString("Name",showDVR.name)
                            val showCameras= CameraListFragment()
                            showCameras.arguments=bundle

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container,showCameras)
                                .addToBackStack(null)
                                .commit()
                        }
                        binding.rvDvrList.adapter=adapter
                    }
                    else{
                        val error=response.errorBody()?.string()
                        Toast.makeText(requireContext(),"Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (ex:Exception){
            Log.e("APIError", "err: ${ex.localizedMessage}")
        }
    }
    fun showAddDVRDialog(){
        val dialogBinding= DialogAddDvrBinding.inflate(layoutInflater)
        val builder= AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog=builder.create()
        // Transparent Background
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnSaveDvr.setOnClickListener {
            val name=dialogBinding.etDvrName.text.toString()
            val ip=dialogBinding.etDvrIp.text.toString()
            val mac=dialogBinding.etDvrMac.text.toString()
            val channels=dialogBinding.etDvrChannels.text.toString().toInt()
            val password=dialogBinding.etDvrPassword.text.toString()
            if (name.isEmpty() || ip.isEmpty() || mac.isEmpty() || password.isEmpty() || channels==0) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addDvr(mac,name,ip,channels,password)
            dialog.dismiss()
        }
        dialog.show()
    }
    fun addDvr(
        mac:String,
        name: String,
        ip: String,
        channels:Int,
        password: String
    ){
        try {
            val admin_id=sharedPrefences.getString("user_id","No Session Occur").toString()
            val dvr= AddDVR(ip,mac,name,password,admin_id,channels)
            lifecycleScope.launch(Dispatchers.IO){
                val api= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=api.add_dvr(dvr)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val body=response.body()?.string()
                        Toast.makeText(requireContext(),body, Toast.LENGTH_SHORT).show()
                        fetchDvrs()
                    }else{
                        val error=response.errorBody()?.string()
                        Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (ex:Exception){
            Log.e("UPLOAD", "err: ${ex.localizedMessage}")
        }
    }
}