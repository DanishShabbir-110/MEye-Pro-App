package com.example.meye_proapplication.FastAPI.APIModels

data class AllocationSuccess(
    val courseName: String,
    val discipline: String,
    val section: String,
    val semester: Int,
    val session: String,
    val teacherName: String
)