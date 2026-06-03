package com.example.examinationofconscience.data.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examinationofconscience.remote.NewsResponse
import com.example.examinationofconscience.remote.RetrofitClient
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _newsList = mutableStateOf<List<NewsResponse>>(emptyList())
    val newsList: State<List<NewsResponse>> = _newsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Stan ostatnio odczytanego ID
    private val _lastReadId = mutableStateOf(0)
    val lastReadId: State<Int> = _lastReadId

    fun fetchNews(context: Context) {
        // Inicjalizacja lastReadId z preferencji przy pierwszym pobraniu
        if (_lastReadId.value == 0) {
            _lastReadId.value = context.getSharedPreferences("prefs_news", Context.MODE_PRIVATE)
                .getInt("last_read_news_id", 0)
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getNewsFeed()
                if (response.isSuccessful) {
                    _newsList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funkcja wywoływana po wejściu w NewsScreen
    fun markAllAsRead(context: Context) {
        val latestId = _newsList.value.maxOfOrNull { it.id } ?: 0
        if (latestId > 0) {
            context.getSharedPreferences("prefs_news", Context.MODE_PRIVATE)
                .edit().putInt("last_read_news_id", latestId).apply()
            _lastReadId.value = latestId // To wyzwoli odświeżenie kropki w MainScreen
        }
    }
}