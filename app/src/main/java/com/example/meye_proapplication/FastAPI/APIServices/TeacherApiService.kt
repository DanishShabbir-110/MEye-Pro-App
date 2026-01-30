package com.example.meye_proapplication.FastAPI.APIServices

import com.example.meye_proapplication.FastAPI.APIModels.ShowSchedule
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface TeacherApiService {
    @GET("/teacher/getTeacherSchedule")
    suspend fun getTeacherSchedule(
        @Query("teacherId")teacherId: String
    ): Response<ShowSchedule>
}