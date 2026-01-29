package com.example.meye_proapplication.FastAPI.APIModels

import com.google.gson.annotations.SerializedName

data class DVRResponse(
    val totalDVRS:Int,
    @SerializedName("DVR")
    val dvrs:List<ShowDVR>
)
