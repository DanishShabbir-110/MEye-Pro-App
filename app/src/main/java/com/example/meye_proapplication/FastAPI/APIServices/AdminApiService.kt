package com.example.meye_prowithtimetableattendance.FastAPI.APIServices

import com.example.meye_proapplication.FastAPI.APIModels.TeacherResponse
import com.example.meye_prowithtimetableattendance.FastAPI.APIModels.User
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AdminApiService {
    @Multipart
    @POST("/admin/AddTeacher")
    suspend fun addTeacher(
        @Part("teacher_id")teacher_id: RequestBody,
        @Part("name")name: RequestBody,
        @Part("Password")password: RequestBody,
        @Part teachers_pics: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @GET("/admin/getAllTeachers")
    suspend fun getAllTeachers(): Response<TeacherResponse>
}