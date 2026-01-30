package com.example.meye_proapplication.Admin.Fragments

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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.meye_proapplication.Datacell.Fragments.CourseAllocationFragment.Companion.EXCEL_PICK_CODE
import com.example.meye_proapplication.Datacell.Fragments.CourseEnrollmentFragment
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentUploadTimetableBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.AdminApiService
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

class UploadTimetableFragment : Fragment() {

    private var selectedExcelFile: File? = null
    private val binding: FragmentUploadTimetableBinding by lazy {
        FragmentUploadTimetableBinding.inflate(layoutInflater)
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
        binding.ivFileIcon.setOnClickListener {
            val intent= Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "application/vnd.ms-excel" // .xls
            ))
            startActivityForResult(intent, CourseEnrollmentFragment.Companion.EXCEL_PICK_CODE)
        }
        binding.btnUpload.setOnClickListener {
            if (selectedExcelFile==null){
                Toast.makeText(requireContext(), "Please select an Excel file first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                uploadTimeTable(selectedExcelFile)
                binding.tvFileName.text="Tap to Select Timetable File"
                selectedExcelFile=null
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
                    binding.tvFileName.text = getFileName(excelUri)
                    Toast.makeText(requireContext(), "Excel File Selected ✅", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "File Corrupted or Unreadable", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Ghalat File! Sirf Excel select karein.", Toast.LENGTH_LONG).show()
                selectedExcelFile = null
                binding.tvFileName.text = "No file selected"
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
    fun uploadTimeTable(
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
                val api= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=api.upload_and_update_timetable(part)
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