package com.example.meye_proapplication.FastAPI.APIModels

data class AddDVR(
    val IP: String,
    val MAC: String,
    val Name: String,
    val Password: String,
    val admin_id: String,
    val channel: Int
)
