package com.example.cafeapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creates a single instance of DataStore for the app
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val DEVICE_ROLE = stringPreferencesKey("device_role")
    }

    // Exposes the role as a continuous stream of data
    val deviceRole: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[DEVICE_ROLE]
        }

    // Asynchronously writes to disk
    suspend fun saveDeviceRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_ROLE] = role
        }
    }

    // Clears the role on logout
    suspend fun clearDeviceRole() {
        context.dataStore.edit { preferences ->
            preferences.remove(DEVICE_ROLE)
        }
    }
}