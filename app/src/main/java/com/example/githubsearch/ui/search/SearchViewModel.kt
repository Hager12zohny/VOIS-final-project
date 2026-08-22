package com.example.githubsearch.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubsearch.data.model.GitHubUser
import com.example.githubsearch.data.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val hasSearched: Boolean = false,
    // Cached results shown immediately on cold start, before any new search runs
    val cachedResults: List<GitHubUser> = emptyList()
)

class SearchViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val queryFlow = MutableStateFlow("")

    /**
     * Paged results stream, re-created whenever the query changes.
     * cachedIn(viewModelScope) survives configuration changes.
     */
    val pagedUsers: Flow<PagingData<GitHubUser>> = queryFlow
        .flatMapLatest { query ->
            if (query.isBlank()) emptyFlow() else repository.searchUsersPaged(query)
        }
        .cachedIn(viewModelScope)

    init {
        restoreLastSearch()
    }

    private fun restoreLastSearch() {
        viewModelScope.launch {
            val lastQuery = repository.lastQuery
            val cached = repository.getCachedResults()
            _uiState.value = _uiState.value.copy(
                query = "",
                cachedResults = cached
            )
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun onSearchTriggered() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return
        _uiState.value = _uiState.value.copy(hasSearched = true)
        queryFlow.value = query
    }

    /** Called by the UI once the first page of live results has loaded, to persist them. */
    fun onResultsLoaded(results: List<GitHubUser>) {
        val query = _uiState.value.query.trim()
        if (query.isBlank() || results.isEmpty()) return
        viewModelScope.launch {
            repository.saveLastSearch(query, results)
        }
    }
}
