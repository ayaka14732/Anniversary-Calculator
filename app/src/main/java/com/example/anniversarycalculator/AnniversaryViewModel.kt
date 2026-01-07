package com.example.anniversarycalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class AnniversaryViewModel(private val repository: AnniversaryRepository) : ViewModel() {

    private val _anniversaries = MutableStateFlow<List<Anniversary>>(emptyList())
    val anniversaries: StateFlow<List<Anniversary>> = _anniversaries.asStateFlow()

    private val _language = MutableStateFlow("da")
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        loadAnniversaries()
    }

    private fun loadAnniversaries() {
        viewModelScope.launch {
            repository.anniversariesFlow.collect { list ->
                _anniversaries.value = list
            }
        }
    }

    fun addAnniversary(title: String, date: LocalDate) {
        viewModelScope.launch {
            val anniversary = Anniversary(title = title, date = date)
            repository.addAnniversary(anniversary)
        }
    }

    fun deleteAnniversary(id: Long) {
        viewModelScope.launch {
            repository.deleteAnniversary(id)
        }
    }

    fun toggleLanguage() {
        _language.value = when (_language.value) {
            "da" -> "zh"
            "zh" -> "en"
            else -> "da"
        }
    }

    fun calculateDifference(targetDate: LocalDate): DateDifference {
        val now = LocalDate.now()

        if (now == targetDate) {
            return DateDifference(isToday = true)
        }

        val isPast = targetDate.isBefore(now)
        val period = if (isPast) {
            Period.between(targetDate, now)
        } else {
            Period.between(now, targetDate)
        }

        return DateDifference(
            years = period.years, months = period.months, days = period.days, isPast = isPast
        )
    }
}
