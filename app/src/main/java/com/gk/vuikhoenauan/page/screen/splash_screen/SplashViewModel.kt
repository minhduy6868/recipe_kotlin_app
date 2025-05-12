package com.gk.vuikhoenauan.page.screen.splash_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gk.vuikhoenauan.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isFirst = sharedPreferences.getBoolean("isFirstLaunch", true)
        _isFirstLaunch.value = isFirst
    }

    fun setFirstLaunch(isFirst: Boolean) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isFirstLaunch", isFirst).apply()
        _isFirstLaunch.value = isFirst
    }
}