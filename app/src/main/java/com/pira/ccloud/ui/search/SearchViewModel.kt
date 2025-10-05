package com.pira.ccloud.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = SearchRepository()
    
    var searchResults by mutableStateOf<List<Poster>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var searchQuery by mutableStateOf("")
        private set
    
    private var searchJob: Job? = null
    
    fun updateSearchQuery(query: String) {
        searchQuery = query
        // Cancel any existing search job
        searchJob?.cancel()
        
        // If query is not empty, start a new search after a delay
        if (query.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                // Add a small delay to avoid too many API calls while typing
                delay(500)
                search(query)
            }
        } else {
            // Clear results if query is empty
            searchResults = emptyList()
        }
    }
    
    private fun search(query: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null
                
                val result = repository.search(query)
                searchResults = result.posters
            } catch (e: Exception) {
                errorMessage = e.message
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
    
    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        errorMessage = null
    }
}