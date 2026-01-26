package com.example.meye_proapplication.Datacell.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.meye_proapplication.R
import com.example.meye_proapplication.databinding.FragmentAddStudentBinding
import com.example.meye_prowithtimetableattendance.FastAPI.APIServices.DatacellApiService
import com.example.meye_prowithtimetableattendance.FastAPI.Client.RetrofitClient
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddStudentFragment : Fragment() {
    private var imagesUriList=mutableListOf<Uri>()
    private var selectedImageView=-1
    private val binding: FragmentAddStudentBinding by lazy {
        FragmentAddStudentBinding.inflate(layoutInflater)
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
        val disciplineList=arrayOf("BSCS","BSAI","BSSE")
        val adapter= ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,disciplineList)
        binding.spStuDiscipline.setAdapter(adapter)

        binding.ivStuFrontImg1.setOnClickListener { openImagePicker(0) }
        binding.ivStuFrontImg2.setOnClickListener { openImagePicker(1) }
        binding.ivStuLeftImg.setOnClickListener { openImagePicker(2) }
        binding.ivStuRightImg.setOnClickListener { openImagePicker(3) }
        binding.btnAddStudent.setOnClickListener {
            val stuRegNo=binding.etStuRegno.text.toString()
            val stuName=binding.etStuName.text.toString()
            val stuPassword=binding.etStuPassword.text.toString()
            val stuDiscipline=binding.spStuDiscipline.text.toString()
            val stuSession=binding.etStuSession.text.toString()
            if(stuRegNo.isEmpty()||
                stuName.isEmpty()||
                stuPassword.isEmpty()||
                stuSession.isEmpty()){
                Toast.makeText(requireContext(), "Please Enter the Complete Data!", Toast.LENGTH_SHORT).show()
            }
            val files=imagesUriList?.let { ImagesToFiles(requireContext(),it) }
            addStudent(stuRegNo,stuName,stuPassword,stuDiscipline,stuSession,files)

        }
    }
    private fun openImagePicker(imageView:Int){
        selectedImageView=imageView
        ImagePicker.with(this)
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
                0->Glide.with(this).load(imageUri).into(binding.ivStuFrontImg1)
                1->Glide.with(this).load(imageUri).into(binding.ivStuFrontImg2)
                2->Glide.with(this).load(imageUri).into(binding.ivStuLeftImg)
                3->Glide.with(this).load(imageUri).into(binding.ivStuRightImg)
            }
        }
        else if(resultCode== ImagePicker.RESULT_ERROR){
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun ImagesToFiles(context: Context,uriList:List<Uri>):List<File>{
        val files=mutableListOf<File>()
        for (uri in uriList){
            val input=context.contentResolver.openInputStream(uri)
            val file=File(context.cacheDir,"upload_${System.currentTimeMillis()}.jpg")
            val output= FileOutputStream(file)
            input?.copyTo(output)
            output.close()
            input?.close()

            files.add(file)
        }
        return files
    }
    private fun addStudent(
        regno:String,
        name:String,
        password:String,
        discipline:String,
        session:String,
        files:List<File>?
    ){
        try{
            val regnoReq=regno.toRequestBody("text/plain".toMediaTypeOrNull())
            val nameReq=name.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordReq=password.toRequestBody("text/plain".toMediaTypeOrNull())
            val disciplineReq=discipline.toRequestBody("text/plain".toMediaTypeOrNull())
            val sessionReq=session.toRequestBody("text/plain".toMediaTypeOrNull())
            val filesPart=files?.map {file ->
                val reqFile=file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "student_pics",
                    file.name,
                    reqFile
                )
            }?: emptyList()
            lifecycleScope.launch(Dispatchers.IO) {
                val datacellAPI= RetrofitClient.retrofit.create(DatacellApiService::class.java)
                val response=datacellAPI.addStudent(
                    regnoReq,
                    nameReq,
                    passwordReq,
                    disciplineReq,
                    sessionReq,
                    filesPart
                )
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        val rawJson = response.body()?.string()
                        Toast.makeText(requireContext(), "Student Added: $rawJson", Toast.LENGTH_SHORT).show()
                        binding.ivStuFrontImg1.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivStuFrontImg2.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivStuLeftImg.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.ivStuRightImg.setImageResource(R.drawable.ic_launcher_foreground)
                        binding.etStuName.text=null
                        binding.etStuRegno.text=null
                        binding.etStuPassword.text=null
                        binding.etStuSession.text=null
                        binding.spStuDiscipline.setText(null,false)
                    }else{
                        val errorMessage=response.errorBody()?.string()
                        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        catch (ex: Exception){
            Log.e("UPLOAD", "err: ${ex.localizedMessage}")
        }
    }
}