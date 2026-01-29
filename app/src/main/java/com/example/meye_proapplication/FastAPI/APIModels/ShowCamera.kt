package com.example.meye_proapplication.FastAPI.APIModels

import com.google.gson.annotations.SerializedName

data class ShowCamera(
    @SerializedName("venue")
    val venue: String,

    @SerializedName("channel")
    val channel: Int,

    @SerializedName("placement")
    val viewType: String,

    @SerializedName("status")
    val status: String
)
data class CameraResponse(
    val total: Int,

    @SerializedName("Cameras")
    val cameras: List<ShowCamera>
)

data class VenueCameraGroup(
    val venueName: String,
    var frontChannel: Int? = null,
    var frontStatus: String? = null, // Naya variable
    var backChannel: Int? = null,
    var backStatus: String? = null   // Naya variable
)
