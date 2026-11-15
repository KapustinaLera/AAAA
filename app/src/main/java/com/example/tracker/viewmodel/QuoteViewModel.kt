package com.example.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.network.QuoteResponse // <-- Импорт из network
import com.example.tracker.network.RetrofitClient // <-- Импорт из network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class QuoteState {
    object Loading : QuoteState()
    data class Success(val quote: QuoteResponse) : QuoteState()
    data class Error(val message: String) : QuoteState()
}

class QuoteViewModel : ViewModel() {
    private val _quoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    val quoteState: StateFlow<QuoteState> = _quoteState

    init {
        fetchQuote()
    }

    fun fetchQuote() {
        viewModelScope.launch {
            _quoteState.value = QuoteState.Loading
            try {
                val quote = RetrofitClient.api.getRandomQuote().first()
                _quoteState.value = QuoteState.Success(quote)
            } catch (e: Exception) {
                _quoteState.value = QuoteState.Error("Ошибка сети. Проверьте интернет.")
            }
        }
    }
}