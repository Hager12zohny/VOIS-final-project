package com.example.githubsearch.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.githubsearch.data.model.GitHubUserDetails
import com.example.githubsearch.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val details: GitHubUserDetails) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

class DetailsViewModel(
    private val repository: GitHubRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            _uiState.value = try {
                DetailsUiState.Success(repository.getUserDetails(username))
            } catch (e: Exception) {
                DetailsUiState.Error(e.message ?: "Failed to load user details")
            }
        }
    }
}

class DetailsViewModelFactory(
    private val repository: GitHubRepository,
    private val username: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
