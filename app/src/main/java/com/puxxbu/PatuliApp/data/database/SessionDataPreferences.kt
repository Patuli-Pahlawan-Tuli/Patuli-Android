package com.puxxbu.PatuliApp.data.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.puxxbu.PatuliApp.data.model.UserDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionDataPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<UserDataModel> {
        return dataStore.data.map { preferences ->
            UserDataModel(
                preferences[NAME_KEY] ?: "",
                preferences[USER_ID_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveSession(session: UserDataModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = session.name
            preferences[USER_ID_KEY] = session.userId
            preferences[TOKEN_KEY] = session.token
            preferences[STATE_KEY] = session.isLogin
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: SessionDataPreferences? = null
        private val NAME_KEY = stringPreferencesKey("name")
        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")


        fun getInstance(dataStore: DataStore<Preferences>): SessionDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}