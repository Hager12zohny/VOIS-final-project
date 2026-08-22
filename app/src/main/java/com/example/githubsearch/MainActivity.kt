package com.example.githubsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.githubsearch.data.api.RetrofitInstance
import com.example.githubsearch.data.local.SearchCacheManager
import com.example.githubsearch.data.repository.GitHubRepository
import com.example.githubsearch.ui.navigation.AppNavGraph
import com.example.githubsearch.ui.theme.GitHubSearchAppTheme

class MainActivity : ComponentActivity() {

    // Simple manual DI - swap for Hilt/Koin as the app grows.
    private val repository by lazy {
        GitHubRepository(
            api = RetrofitInstance.api,
            cacheManager = SearchCacheManager(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitHubSearchAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(repository = repository)
                }
            }
        }
    }
}
