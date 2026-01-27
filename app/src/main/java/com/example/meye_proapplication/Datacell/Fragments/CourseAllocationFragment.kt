package com.example.meye_proapplication.Datacell.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.meye_proapplication.Datacell.Fragments.CourseEnrollmentFragment.Companion.EXCEL_PICK_CODE
import com.example.meye_proapplication.FastAPI.APIModels.AllocationSuccess
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentCourseAllocationBinding
import com.example.meye_proapplication.databinding.FragmentCourseEnrollmentBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.DatacellApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


class CourseAllocationFragment : Fragment() {
    companion object {
        const val EXCEL_PICK_CODE = 101
    }
    private var selectedExcelFile: File? = null
    private val binding: FragmentCourseAllocationBinding by lazy {
        FragmentCourseAllocationBinding.inflate(layoutInflater)
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

        val semestersList=arrayOf("1","2","3","4","5","6","7","8")
        val adapter= ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,semestersList)
        binding.spSemester.setAdapter(adapter)
        val disciplineList=arrayOf("BSCS","BSAI","BSSE")
        val disciplineAdapter= ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,disciplineList)
        binding.spDiscipline.setAdapter(disciplineAdapter)

        binding.rgAllocationType.setOnCheckedChangeListener {_,checkedId ->
            if(checkedId==R.id.rbSingle){
                binding.layoutSingleForm.visibility=View.VISIBLE
                binding.layoutBulkForm.visibility=View.GONE
            }
            else if(checkedId==R.id.rbBulk){
                binding.layoutBulkForm.visibility=View.VISIBLE
                binding.layoutSingleForm.visibility=View.GONE
            }
        }

        binding.cardUpload.setOnClickListener {
            val intent= Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "application/vnd.ms-excel" // .xls
            ))
            startActivityForResult(intent, CourseEnrollmentFragment.Companion.EXCEL_PICK_CODE)
        }
        binding.btnAllocate.setOnClickListener {
            if (binding.rbBulk.isChecked && selectedExcelFile == null) {
                Toast.makeText(requireContext(), "Please select an Excel file first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.rbSingle.isChecked){
                val allocationTeacherName=binding.etTeacherName.text.toString()
                val courseName=binding.etCourseName.text.toString()
                val section=binding.etSection.text.toString()
                val semester=binding.spSemester.text.toString().toInt()
                val discipline=binding.spDiscipline.text.toString()
                val session=binding.etSession.text.toString()
                if(
                    allocationTeacherName.isEmpty()||
                    courseName.isEmpty()||
                    section.isEmpty()||
                    session.isEmpty()
                ){
                    Toast.makeText(requireContext(), "Please Enter the Complete Data!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                singleAllocation(courseName,allocationTeacherName,discipline,session,section,semester)
            }else{
                uploadExcelForAllocation(selectedExcelFile)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == EXCEL_PICK_CODE) {
            val excelUri = data?.data ?: return

            // Android System se file ki asliyat (Type) poochein
            val contentResolver = requireContext().contentResolver
            val type = contentResolver.getType(excelUri)
            val fileName = getFileName(excelUri).lowercase()


            if (
                type == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
                type == "application/vnd.ms-excel" ||
                fileName.endsWith(".xls") ||
                fileName.endsWith(".xlsx")
            ) {
                try {
                    selectedExcelFile = ExcelToFile(requireContext(), excelUri)
                    binding.excelName.text = getFileName(excelUri)
                    Toast.makeText(requireContext(), "Excel File Selected ✅", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "File Corrupted or Unreadable", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Ghalat File! Sirf Excel select karein.", Toast.LENGTH_LONG).show()
                selectedExcelFile = null
                binding.excelName.text = "No file selected"
            }
        } else {
            Toast.makeText(requireContext(), "Selection Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    fun getFileName(uri: Uri): String {
        var name = "selected_file.xls"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(
                    it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                )
            }
        }
        return name
    }
    private fun ExcelToFile(context: Context, uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.xlsx")
        val output = FileOutputStream(file)
        input?.copyTo(output)
        output.close()
        input?.close()
        return file
    }
    fun singleAllocation(
        courseName:String,
        teacherName:String,
        discipline: String,
        session: String,
        section:String,
        semester:Int
    ){
        try{
            val singleAllocaton= AllocationSuccess(courseName,discipline,section,semester,session,teacherName)
            lifecycleScope.launch(Dispatchers.IO) {
                val datacellAPI= RetrofitClient.retrofit.create(DatacellApiService::class.java)
                val response=datacellAPI.single_allocation(singleAllocaton)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val body=response.body()?.string()
                        Toast.makeText(requireContext(), "$body", Toast.LENGTH_SHORT).show()
                        binding.etTeacherName.text=null
                        binding.etCourseName.text=null
                        binding.etSection.text=null
                        binding.etSession.text=null
                        binding.spSemester.setText(null, false)
                        binding.spDiscipline.setText(null,false)
                    }
                    else{
                        val errorBody=response.errorBody()?.string()
                        Toast.makeText(requireContext(), "$errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        catch(ex: Exception){
            Log.e("UPLOAD", "err: ${ex.localizedMessage}")
        }
    }
    fun uploadExcelForAllocation(
        excelSheet:File?
    ){
        try {
            if (excelSheet == null) {
                Toast.makeText(requireContext(), "No file selected!", Toast.LENGTH_SHORT).show()
                return
            }
            val reqFile = excelSheet.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
            val part=MultipartBody.Part.createFormData("file", excelSheet.name, reqFile)
            lifecycleScope.launch(Dispatchers.IO) {
                val api= RetrofitClient.retrofit.create(DatacellApiService::class.java)
                val response=api.upload_allocation_excel(part)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val body=response.body()?.string()
                        Toast.makeText(requireContext(), "$body", Toast.LENGTH_SHORT).show()
                    }else{
                        val errorBody=response.errorBody()?.string()
                        Toast.makeText(requireContext(), "$errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (ex: Exception){
            Log.e("UPLOAD", "err: ${ex.localizedMessage}")
        }
    }
}