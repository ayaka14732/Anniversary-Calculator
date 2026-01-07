package com.example.anniversarycalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnniversaryViewModelFactory(
    private val repository: AnniversaryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnniversaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AnniversaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
