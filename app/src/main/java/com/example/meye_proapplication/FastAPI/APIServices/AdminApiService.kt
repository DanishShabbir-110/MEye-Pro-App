package com.example.meye_prowithtimetableattendance.FastAPI.APIServices

import androidx.core.location.LocationRequestCompat
import com.example.meye_proapplication.FastAPI.APIModels.AddCamera
import com.example.meye_proapplication.FastAPI.APIModels.AddDVR
import com.example.meye_proapplication.FastAPI.APIModels.CameraResponse
import com.example.meye_proapplication.FastAPI.APIModels.DVRResponse
import com.example.meye_proapplication.FastAPI.APIModels.TeacherResponse
import com.example.meye_prowithtimetableattendance.FastAPI.APIModels.User
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AdminApiService {
    @Multipart
    @POST("/admin/AddTeacher")
    suspend fun addTeacher(
        @Part("teacher_id")teacher_id: RequestBody,
        @Part("name")name: RequestBody,
        @Part("Password")password: RequestBody,
        @Part teachers_pics: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @Multipart
    @POST("/admin/AddOtherStaff")
    suspend fun addOtherStaff(
        @Part("id")id: RequestBody,
        @Part("name")name: RequestBody,
        @Part("Password")Password: RequestBody,
        @Part("Role")Role: RequestBody,
        @Part profileImg: MultipartBody.Part?
    ): Response<ResponseBody>
    @GET("/admin/getAllTeachers")
    suspend fun getAllTeachers(): Response<TeacherResponse>

    @POST("/admin/AddDVR")
    suspend fun add_dvr(
        @Body dvr: AddDVR
    ): Response<ResponseBody>

    @POST("/admin/addCamera")
    suspend fun add_camera(
        @Body cam: AddCamera
    ): Response<ResponseBody>

    @GET("/admin/getALLDVR")
    suspend fun getAllDVRS(): Response<DVRResponse>

    @GET("/admin/getCamerasByDvrID")
    suspend fun getCameras(
        @Query("dvr_id")dvr_id: String
    ):Response<CameraResponse>
}