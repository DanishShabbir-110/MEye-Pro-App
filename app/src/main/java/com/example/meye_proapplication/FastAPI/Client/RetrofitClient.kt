package com.example.meye_prowithtimetableattendance.FastAPI.Client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    const val baseUrl="http://192.168.1.88:8000/"
    private val logging= HttpLoggingInterceptor().apply{
        level= HttpLoggingInterceptor.Level.BODY
    }
    private val client= OkHttpClient.Builder()
        .addInterceptor(logging)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}