package com.example.meye_proapplication.FastAPI.APIModels

data class TeacherResponse(
    val total: Int,
    val teachers: List<ShowTeacher>
)
