package com.example.meye_proapplication.Datacell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.meye_proapplication.Datacell.Fragments.AddStudentFragment
import com.example.meye_proapplication.Datacell.Fragments.CourseEnrollmentFragment
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentAddTeacherBinding
import com.example.meye_proapplication.databinding.FragmentDatacellBinding


class DatacellFragment : Fragment() {
    private val binding: FragmentDatacellBinding by lazy{
        FragmentDatacellBinding.inflate(layoutInflater)
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
        if (savedInstanceState == null) {
            replaceFragment(AddStudentFragment())
        }
        binding.bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.nav_add_student -> AddStudentFragment()
                R.id.nav_enrollment -> CourseEnrollmentFragment()
                R.id.nav_allocation -> AddStudentFragment()
                else -> null
            }
            selectedFragment?.let {
                replaceFragment(it)
                return@setOnItemSelectedListener true
            }
            false
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}