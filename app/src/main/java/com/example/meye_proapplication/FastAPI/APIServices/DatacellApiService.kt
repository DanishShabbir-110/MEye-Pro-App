package com.example.meye_prowithtimetableattendance.FastAPI.APIServices

import com.example.meye_prowithtimetableattendance.FastAPI.APIModels.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DatacellApiService {
    @Multipart
    @POST("/datacell/AddStudent")
    suspend fun addStudent(
        @Part("Regno")regno: RequestBody,
        @Part("name")name: RequestBody,
        @Part("Password")password: RequestBody,
        @Part("discipline")discipline: RequestBody,
        @Part("session")session: RequestBody,
        @Part student_pics: List<MultipartBody.Part>
    ): Response<ResponseBody>
    @FormUrlEncoded
    @POST("/datacell/singleEnrollmentofStudent")
    suspend fun singleEnrollmentofStudent(
        @Field("Regno")Regno:String,
        @Field("courseName")courseName:String,
        @Field("section")section:String,
        @Field("semester")semester:Int,
        @Field("session")session:String
    ): Response<ResponseBody>
    @Multipart
    @POST("/datacell/UploadEnrollmentExcel")
    suspend fun upload_enrollment_excel(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>
}