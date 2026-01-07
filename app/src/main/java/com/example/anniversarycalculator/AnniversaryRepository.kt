package com.example.anniversarycalculator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anniversaries")

class AnniversaryRepository(private val context: Context) {
    private val gson = Gson()
    private val anniversariesKey = stringPreferencesKey("anniversaries_list")

    val anniversariesFlow: Flow<List<Anniversary>> = context.dataStore.data.map { preferences ->
        val json = preferences[anniversariesKey] ?: "[]"
        val type = object : TypeToken<List<AnniversaryDto>>() {}.type
        val dtos: List<AnniversaryDto> = gson.fromJson(json, type)
        dtos.map { it.toAnniversary() }
    }

    suspend fun addAnniversary(anniversary: Anniversary) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[anniversariesKey] ?: "[]"
            val type = object : TypeToken<MutableList<AnniversaryDto>>() {}.type
            val list: MutableList<AnniversaryDto> = gson.fromJson(currentJson, type)
            list.add(AnniversaryDto.fromAnniversary(anniversary))
            preferences[anniversariesKey] = gson.toJson(list)
        }
    }

    suspend fun deleteAnniversary(id: Long) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[anniversariesKey] ?: "[]"
            val type = object : TypeToken<MutableList<AnniversaryDto>>() {}.type
            val list: MutableList<AnniversaryDto> = gson.fromJson(currentJson, type)
            list.removeIf { it.id == id }
            preferences[anniversariesKey] = gson.toJson(list)
        }
    }
}

// DTO for JSON serialization
data class AnniversaryDto(
    val id: Long, val title: String, val dateString: String
) {
    fun toAnniversary(): Anniversary = Anniversary(
        id = id, title = title, date = LocalDate.parse(dateString)
    )

    companion object {
        fun fromAnniversary(anniversary: Anniversary): AnniversaryDto = AnniversaryDto(
            id = anniversary.id, title = anniversary.title, dateString = anniversary.date.toString()
        )
    }
}
