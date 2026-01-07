package com.example.anniversarycalculator

import java.time.LocalDate

data class Anniversary(
    val id: Long = System.currentTimeMillis(), val title: String, val date: LocalDate
)
