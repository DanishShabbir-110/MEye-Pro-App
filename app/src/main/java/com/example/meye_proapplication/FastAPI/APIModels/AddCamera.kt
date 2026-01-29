package com.example.meye_proapplication.FastAPI.APIModels

data class AddCamera(
    val IP: String,
    val channel_no: Int,
    val dvr_id: String,
    val mac: String,
    val placement: String,
    val resolution: String,
    val status: String,
    val venue_id: String
)