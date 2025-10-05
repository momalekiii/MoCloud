package com.pira.ccloud.ui.series

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.repository.SeriesRepository
import kotlinx.coroutines.launch

class SeriesViewModel : ViewModel() {
    private val repository = SeriesRepository()
    
    var series by mutableStateOf<List<Series>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var isLoadingMore by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var currentPage by mutableStateOf(0)
        private set
    
    var canLoadMore by mutableStateOf(true)
        private set
    
    init {
        loadSeries()
    }
    
    fun loadSeries(page: Int = 0) {
        viewModelScope.launch {
            try {
                if (page == 0) {
                    isLoading = true
                } else {
                    isLoadingMore = true
                }
                errorMessage = null
                
                val newSeries = repository.getSeries(page)
                
                // If we get fewer series than expected, we've reached the end
                canLoadMore = newSeries.isNotEmpty()
                
                if (page == 0) {
                    series = newSeries
                } else {
                    series = series + newSeries
                }
                
                currentPage = page
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
                isLoadingMore = false
            }
        }
    }
    
    fun loadMoreSeries() {
        if (!isLoading && !isLoadingMore && canLoadMore) {
            loadSeries(currentPage + 1)
        }
    }
    
    fun retry() {
        loadSeries(currentPage)
    }
    
    fun refresh() {
        loadSeries(0)
    }
}