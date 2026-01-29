package com.example.meye_proapplication.Admin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentAddCameraBinding

class AddCameraFragment : Fragment() {
    private val binding: FragmentAddCameraBinding by lazy {
        FragmentAddCameraBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}