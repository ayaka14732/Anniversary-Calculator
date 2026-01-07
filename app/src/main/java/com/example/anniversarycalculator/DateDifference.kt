package com.example.anniversarycalculator

data class DateDifference(
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val isPast: Boolean = false,
    val isToday: Boolean = false
)
