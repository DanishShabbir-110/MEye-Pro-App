package com.example.meye_proapplication.Admin.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentAddTeacherBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.AdminApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddTeacherFragment : Fragment() {
    private var imagesUriList=mutableMapOf<Int, Uri>()
    private var selectedImageView=-1
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roles=arrayOf("Datacell","Admin")
        val adapter= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,roles)
        binding.spStaffRole.setAdapter(adapter)
        binding.ivFrontImg1.setOnClickListener {openImagePicker(0) }
        binding.ivFrontImg2.setOnClickListener {openImagePicker(1)}
        binding.ivLeftImg.setOnClickListener {openImagePicker(2)}
        binding.ivRightImg.setOnClickListener {openImagePicker(3) }
        binding.ivStaffProfile.setOnClickListener { openImagePicker(4) }
        binding.rgRegistrationType.setOnCheckedChangeListener {_,checkedId ->
            if(checkedId==R.id.rbTeacher){
                binding.layoutTeacherForm.visibility= View.VISIBLE
                binding.layoutStaffForm.visibility= View.GONE
            }
            else{
                binding.layoutStaffForm.visibility=View.VISIBLE
                binding.layoutTeacherForm.visibility=View.GONE
            }
        }
        binding.btnAddTeacher.setOnClickListener {
            val teacherId=binding.etTeacherId.text.toString()
            val teacherName=binding.etTeacherName.text.toString()
            val teacherPassword=binding.etTeacherPassword.text.toString()
            if(teacherId.isEmpty()||teacherName.isEmpty()||teacherPassword.isEmpty()){
                Toast.makeText(requireContext(), "Please Enter the Complete Data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val files=imagesUriList?.let {ImagesToFiles(requireContext(),imagesUriList.values.toList())}
            addTeacher(teacherId,teacherName,teacherPassword,files)
        }
        binding.btnAddStaff.setOnClickListener {
            val id=binding.etStaffId.text.toString()
            val name=binding.etStaffName.text.toString()
            val password=binding.etStaffPassword.text.toString()
            val role=binding.spStaffRole.text.toString()
            if(id.isEmpty() || name.isEmpty() || password.isEmpty() || role.isEmpty()){
                Toast.makeText(requireContext(), "Please Enter Complete Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val staffImage=imagesUriList[4]
            var staffFile:File?=null
            if (staffImage != null) {
                // Humari helper function List maangti hai, isliye listOf(staffUri) pass kiya
                val convertedFiles = ImagesToFiles(requireContext(), listOf(staffImage))
                // List main se pehli file nikal lein
                if (convertedFiles.isNotEmpty()) {
                    staffFile = convertedFiles[0]
                }
            }
            addOtherStaff(id,name,password,role,staffFile)
        }
    }
    private fun openImagePicker(imageView:Int){
        selectedImageView=imageView
        ImagePicker.Companion.with(this)
//            .crop()
//            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            val imageUri=data?.data?:return
            imagesUriList[selectedImageView]=imageUri
            when(selectedImageView){
                0-> Glide.with(this).load(imageUri).into(binding.ivFrontImg1)
                1-> Glide.with(this).load(imageUri).into(binding.ivFrontImg2)
                2-> Glide.with(this).load(imageUri).into(binding.ivLeftImg)
                3-> Glide.with(this).load(imageUri).into(binding.ivRightImg)
                4->Glide.with(this).load(imageUri).into(binding.ivStaffProfile)
            }
        }
        else if(resultCode== ImagePicker.Companion.RESULT_ERROR){
            Toast.makeText(requireContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun ImagesToFiles(context: Context, uriList: List<Uri>): List<File> {
        val files = mutableListOf<File>()
        for (uri in uriList) {
            val input = context.contentResolver.openInputStream(uri)
            // Har file ka naam unique hona chahiye
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}_${files.size}.jpg")
            val output = FileOutputStream(file)
            input?.copyTo(output)
            output.close()
            input?.close()
            files.add(file)
        }
        return files
    }
    fun addTeacher(
        teacher_Id:String,
        name:String,
        password:String,
        files:List<File>?
    ){
        try{
            val teacherIdReq=teacher_Id.toRequestBody("text/plain".toMediaTypeOrNull())
            val nameReq=name.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordReq=password.toRequestBody("text/plain".toMediaTypeOrNull())
            val filesPart=files?.map {file ->
                val reqFile=file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "teachers_pics",
                    file.name,
                    reqFile
                )
            }?: emptyList()
            lifecycleScope.launch(Dispatchers.IO) {
                val adminAPI= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=adminAPI.addTeacher(
                    teacherIdReq,
                    nameReq,
                    passwordReq,
                    filesPart
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()?.string()
                        Toast.makeText(
                            requireContext(),
                            "Teacher Added: ${body}",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.ivFrontImg1.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivFrontImg2.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivLeftImg.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivRightImg.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.etTeacherId.text = null
                        binding.etTeacherName.text = null
                        binding.etTeacherPassword.text = null
                    } else {
                        val error=response.errorBody()?.string()
                        Toast.makeText(
                            requireContext(),
                            "Failed: ${error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        catch (ex: Exception){
            Log.e("UPLOAD", "err: ${ex.localizedMessage}")
        }
    }
    fun addOtherStaff(
        id:String,
        name:String,
        password:String,
        role:String,
        file:File?
    ){
        try {
            val IdReq=id.toRequestBody("text/plain".toMediaTypeOrNull())
            val nameReq=name.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordReq=password.toRequestBody("text/plain".toMediaTypeOrNull())
            val roleReq=role.toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart=file?.let {
                val reqFile=it.asRequestBody("image/*".toMediaTypeOrNull())

                MultipartBody.Part.createFormData("profileImg",it.name,reqFile)
            }
            lifecycleScope.launch(Dispatchers.IO){
                val api= RetrofitClient.retrofit.create(AdminApiService::class.java)
                val response=api.addOtherStaff(IdReq,nameReq,passwordReq,roleReq,filePart)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val body=response.body()?.string()
                        Toast.makeText(requireContext(),body, Toast.LENGTH_SHORT).show()
                    }
                    else{
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