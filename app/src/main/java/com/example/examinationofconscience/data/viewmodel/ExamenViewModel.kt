package com.example.examinationofconscience.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class SinItem(val id: String, val name: String, val category: String)

class ExamenViewModel : ViewModel() {
    // Używamy mapy dla lepszej stabilności stanu i szybszego wyszukiwania
    private val _checkedSins = mutableStateMapOf<String, SinItem>()
    val checkedSins: List<SinItem> get() = _checkedSins.values.toList()

    // Nowe pola do obsługi wyświetlania tekstu
    var selectedTitle by mutableStateOf("")
    var selectedContent by mutableStateOf("")

    fun selectText(title: String, content: String) {
        selectedTitle = title
        selectedContent = content
    }

    // Zaktualizowana funkcja przyjmująca również kategorię
    fun toggleSin(id: String, name: String, category: String) {
        if (_checkedSins.containsKey(id)) {
            _checkedSins.remove(id)
        } else {
            _checkedSins[id] = SinItem(id, name, category)
        }
    }

    fun removeSin(id: String) {
        _checkedSins.remove(id)
    }

    fun clearAll() {
        _checkedSins.clear()
    }

    fun isChecked(id: String): Boolean = _checkedSins.containsKey(id)
}