package com.example.meye_proapplication.FastAPI.APIServices

import com.example.meye_proapplication.FastAPI.APIModels.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("/Authorization/Login")
    suspend fun login(
        @Field("userId") userId:String,
        @Field("password")password:String
    ): Response<LoginResponse>
}