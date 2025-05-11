package com.gk.news_pro.page.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun updateUiState(state: RegisterUiState) {
        _uiState.value = state
    }

    fun register() {
        when {
            username.value.isBlank() || email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank() -> {
                _uiState.value = RegisterUiState.Error("Vui lòng điền đầy đủ thông tin")
            }
            username.value.length < 3 -> {
                _uiState.value = RegisterUiState.Error("Tên người dùng phải có ít nhất 3 ký tự")
            }
            !email.value.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$")) -> {
                _uiState.value = RegisterUiState.Error("Email không hợp lệ")
            }
            password.value.length < 6 -> {
                _uiState.value = RegisterUiState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            }
            password.value != confirmPassword.value -> {
                _uiState.value = RegisterUiState.Error("Mật khẩu xác nhận không khớp")
            }
            else -> {
                _uiState.value = RegisterUiState.Loading
                viewModelScope.launch {
                    try {
                        val user = repository.createUser(username.value, email.value, password.value)
                        if (user != null) {
                            _uiState.value = RegisterUiState.Success
                        } else {
                            _uiState.value = RegisterUiState.Error("Đăng ký thất bại")
                        }
                    } catch (e: Exception) {
                        val errorMessage = when {
                            e.message?.contains("email already in use") == true -> "Email đã được sử dụng"
                            e.message?.contains("weak password") == true -> "Mật khẩu quá yếu"
                            else -> "Đăng ký thất bại: ${e.message}"
                        }
                        _uiState.value = RegisterUiState.Error(errorMessage)
                    }
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RegisterUiState.Idle
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}