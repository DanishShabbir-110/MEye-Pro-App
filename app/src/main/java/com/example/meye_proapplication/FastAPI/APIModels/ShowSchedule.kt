package com.example.meye_proapplication.FastAPI.APIModels

data class ShowSchedule(
    val Lectures: List<Lecture>,
    val Total_Classes: Int
)