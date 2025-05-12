package com.gk.vuikhoenauan.page.screen.recipe_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.news_pro.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeChatViewModel(
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _chatState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val chatState: StateFlow<ChatUiState> = _chatState

    private val _chatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatHistory: StateFlow<List<Pair<String, String>>> = _chatHistory

    fun askQuestion(recipe: Recipe, question: String) {
        _chatState.value = ChatUiState.Loading
        viewModelScope.launch {
            val result = geminiRepository.askRecipeQuestion(recipe, question)
            val response = if (result.isSuccess) {
                result.getOrNull() ?: "Không có câu trả lời"
            } else {
                result.exceptionOrNull()?.message ?: "Lỗi không xác định"
            }
            _chatHistory.value = _chatHistory.value + Pair(question, response)
            _chatState.value = if (result.isSuccess) {
                ChatUiState.Success(response)
            } else {
                ChatUiState.Error(response)
            }
        }
    }

    fun resetChat() {
        _chatState.value = ChatUiState.Idle
        _chatHistory.value = emptyList()
    }
}

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val response: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}