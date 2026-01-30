package com.example.meye_proapplication.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meye_proapplication.Admin.Fragments.AddTeacherFragment
import com.example.meye_proapplication.Admin.Fragments.DVRListFragment
import com.example.meye_proapplication.Admin.Fragments.HomeFragment
import com.example.meye_proapplication.Admin.Fragments.UploadTimetableFragment
import com.example.meye_proapplication.Datacell.Fragments.AddStudentFragment
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {
    private val binding: FragmentAdminBinding by lazy {
        FragmentAdminBinding.inflate(layoutInflater)
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
        if(savedInstanceState==null){
            replaceFragment(HomeFragment())
        }
        binding.bottomNav.setOnItemSelectedListener {item ->
            val selectedFragment: Fragment?=when(item.itemId){
                R.id.nav_home-> HomeFragment()
                R.id.nav_addUser-> AddTeacherFragment()
                R.id.nav_dvr_setting-> DVRListFragment()
                R.id.nav_upload_timetable-> UploadTimetableFragment()
                else -> null
            }
            selectedFragment?.let {
                replaceFragment(it)
                return@setOnItemSelectedListener true
            }
            false
        }
    }
    private fun replaceFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }
}