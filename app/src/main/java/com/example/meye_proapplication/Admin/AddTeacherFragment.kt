package com.example.meye_proapplication.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meye_proapplication.databinding.FragmentAddTeacherBinding

class AddTeacherFragment : Fragment() {
   private val binding: FragmentAddTeacherBinding by lazy {
       FragmentAddTeacherBinding.inflate(layoutInflater)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


}