package com.example.meye_prowithtimetableattendance.FastAPI.APIModels

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val Full_Name: String,
    val Password: String,
    val Profile_Created_Date: String,
    val Profile_Created_Time: String,
    val Profile_Image_Url: String,
    val Role: String,
    val UID: String
)