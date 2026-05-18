package com.timecapsule.app.data.datastore


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.timecapsule.app.data.model.Capsule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

val Context.dataStore by preferencesDataStore("settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val CAPSULES = stringPreferencesKey("capsules_json")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    val capsulesFlow: Flow<List<Capsule>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[CAPSULES] ?: "[]"
            try {
                Json.decodeFromString<List<Capsule>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit {
            it[DARK_MODE] = enabled
        }
    }

    suspend fun saveCapsules(capsules: List<Capsule>) {
        val jsonString = Json.encodeToString(capsules)
        context.dataStore.edit {
            it[CAPSULES] = jsonString
        }
    }
}