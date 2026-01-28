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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private var imagesUriList=mutableListOf<Uri>()
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
        binding.ivFrontImg1.setOnClickListener {openImagePicker(0) }
        binding.ivFrontImg2.setOnClickListener {openImagePicker(1)}
        binding.ivLeftImg.setOnClickListener {openImagePicker(2)}
        binding.ivRightImg.setOnClickListener {openImagePicker(3) }
        binding.btnAddTeacher.setOnClickListener {
            val teacherId=binding.etTeacherId.text.toString()
            val teacherName=binding.etTeacherName.text.toString()
            val teacherPassword=binding.etTeacherPassword.text.toString()
            if(teacherId.isEmpty()||teacherName.isEmpty()||teacherPassword.isEmpty()){
                Toast.makeText(requireContext(), "Please Enter the Complete Data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val files=imagesUriList?.let {ImagesToFiles(requireContext(),it)}
            addTeacher(teacherId,teacherName,teacherPassword,files)
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
            imagesUriList.add(imageUri)
            when(selectedImageView){
                0-> Glide.with(this).load(imageUri).into(binding.ivFrontImg1)
                1-> Glide.with(this).load(imageUri).into(binding.ivFrontImg2)
                2-> Glide.with(this).load(imageUri).into(binding.ivLeftImg)
                3-> Glide.with(this).load(imageUri).into(binding.ivRightImg)
            }
        }
        else if(resultCode== ImagePicker.Companion.RESULT_ERROR){
            Toast.makeText(requireContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun ImagesToFiles(context: Context, uriList:List<Uri>):List<File>{
        val files=mutableListOf<File>()
        for (uri in uriList){
            val input=context.contentResolver.openInputStream(uri)
            val file= File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val output= FileOutputStream(file)
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
}