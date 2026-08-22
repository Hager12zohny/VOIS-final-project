package com.example.githubsearch.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.githubsearch.data.model.GitHubUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

private val Context.dataStore by preferencesDataStore(name = "search_cache")

/**
 * Persists the last search query + its first page of results so the app can
 * show them immediately on relaunch, per the "Save the last search results
 * locally and display them when the application is reopened" optional requirement.
 */
class SearchCacheManager(private val context: Context) {

    private object Keys {
        val QUERY = stringPreferencesKey("last_query")
        val RESULTS = stringPreferencesKey("last_results")
    }

    @Serializable
    private data class CachedUser(
        val id: Long,
        val login: String,
        val avatarUrl: String,
        val htmlUrl: String,
        val score: Double
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveLastSearch(query: String, results: List<GitHubUser>) {
        val cached = results.map {
            CachedUser(it.id, it.login, it.avatarUrl, it.htmlUrl, it.score)
        }
        context.dataStore.edit { prefs ->
            prefs[Keys.QUERY] = query
            prefs[Keys.RESULTS] = json.encodeToString(cached)
        }
    }

    val lastQuery: Flow<String> = context.dataStore.data.map { it[Keys.QUERY] ?: "" }

    suspend fun getLastResults(): List<GitHubUser> {
        val raw = context.dataStore.data.first()[Keys.RESULTS] ?: return emptyList()
        return try {
            json.decodeFromString<List<CachedUser>>(raw).map {
                GitHubUser(it.id, it.login, it.avatarUrl, it.htmlUrl, it.score)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
