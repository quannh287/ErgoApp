package com.example.ergoguard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ergoguard.model.SeverityLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ergoguard_prefs")

/**
 * Repository để lưu trữ và truy xuất dữ liệu người dùng.
 */
class AppPreferencesRepository(private val context: Context) {

    companion object {
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val HISTORY_JSON = stringPreferencesKey("history_json")
    }

    // Onboarding
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_SEEN_ONBOARDING] ?: false
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs ->
            prefs[HAS_SEEN_ONBOARDING] = true
        }
    }

    // History - Simple JSON storage
    val historyJson: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[HISTORY_JSON] ?: "[]"
    }

    suspend fun saveHistoryJson(json: String) {
        context.dataStore.edit { prefs ->
            prefs[HISTORY_JSON] = json
        }
    }
}

/**
 * Model đơn giản cho lịch sử phân tích.
 */
data class HistoryEntry(
    val timestamp: Long,
    val percentage: Double,
    val level: String,
    val neckLoadKg: Double
)
