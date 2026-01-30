package com.example.meye_proapplication.FastAPI.APIModels

import com.google.gson.annotations.SerializedName


data class Lecture(
    @SerializedName("Class Start time")
    val Class_End_time: String,
    @SerializedName("Class End time")
    val Class_Start_time: String,
    @SerializedName("Course Name")
    val Course_Name: String,
    @SerializedName("Discipline")
    val Discipline: String,
    @SerializedName("Venue")
    val Venue: String
)