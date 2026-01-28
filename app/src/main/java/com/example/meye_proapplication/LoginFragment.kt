package com.example.meye_proapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.meye_proapplication.FastAPI.APIServices.AuthApiService
import com.example.meye_proapplication.databinding.FragmentLoginBinding
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import kotlin.math.log

class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
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
        binding.btnLogin.setOnClickListener {
            val userid=binding.etUserId.text.toString().trim()
            val password=binding.etPassword.text.toString().trim()
            if(userid.isEmpty()||password.isEmpty()){
                Toast.makeText(requireContext(), "Enter Complete Login Credentials", Toast.LENGTH_SHORT).show()
            }
            login(userid,password)
        }
    }
    fun login(
        userID:String,password: String
    ){
        try{
            lifecycleScope.launch(Dispatchers.IO) {
                val authAPI= RetrofitClient.retrofit.create(AuthApiService::class.java)
                val response=authAPI.login(userID,password)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val role=response.body()?.Role
                        when(role){
                            "Datacell"->findNavController().navigate(R.id.datacellFragment2)
                            "Admin"->findNavController().navigate(R.id.adminFragment)
                        }
                    }else{
                        val errorBody=response.errorBody()?.string()
                        Toast.makeText(requireContext(), "$errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch(ex: Exception){
            Log.e("Login", "err: ${ex.localizedMessage}")
        }
    }
}