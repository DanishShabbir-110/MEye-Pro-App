package com.example.meye_proapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.meye_proapplication.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {
    private val binding: FragmentSplashScreenBinding by lazy {
        FragmentSplashScreenBinding.inflate(layoutInflater)
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
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashScreenFragment2, true)
            .build()
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(
                R.id.loginFragment,
                null,
                navOptions
            )
        }, 2000)
    }
}