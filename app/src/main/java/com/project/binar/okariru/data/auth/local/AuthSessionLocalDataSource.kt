package com.project.binar.okariru.data.auth.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.binar.okariru.data.auth.dto.AuthSession
import com.project.binar.okariru.data.auth.dto.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val AUTH_PREFERENCES_NAME = "auth_session"

private val Context.authPreferences: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_PREFERENCES_NAME,
)

class AuthSessionLocalDataSource(context: Context) {

    private val dataStore = context.applicationContext.authPreferences

    fun observe(): Flow<AuthSession?> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map(::toSession)

    suspend fun save(session: AuthSession) {
        dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = session.accessToken
            preferences[Keys.EXPIRES_AT] = session.expiresAtMillis
            preferences[Keys.USER_ROLES] = session.user.roles.toSet()
            preferences[Keys.USER_NAME] = session.user.name
            session.user.id?.let { preferences[Keys.USER_ID] = it } ?: preferences.remove(Keys.USER_ID)
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private fun toSession(preferences: Preferences): AuthSession? {
        val name = preferences[Keys.USER_NAME] ?: return null
        val token = preferences[Keys.ACCESS_TOKEN] ?: return null
        val expiresAt = preferences[Keys.EXPIRES_AT] ?: return null

        return AuthSession(
            user = AuthUser(
                name = name,
                roles = preferences[Keys.USER_ROLES].orEmpty().toList(),
                id = preferences[Keys.USER_ID],
            ),
            accessToken = token,
            expiresAtMillis = expiresAt,
        )
    }

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLES = stringSetPreferencesKey("user_roles")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val EXPIRES_AT = longPreferencesKey("expires_at")
        val USER_ID = intPreferencesKey("user_id")
    }
}
