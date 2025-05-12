package com.gk.news_pro.data.repository

import com.gk.news_pro.page.utils.RetrofitClient
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.service.GeminiApiService
import com.gk.vuikhoenauan.service.GeminiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiRepository {
    private val geminiApiService: GeminiApiService =
        RetrofitClient.geminiRetrofit.create(GeminiApiService::class.java)

    private val apiKey = "AIzaSyB-1CJgvaYhED_HamPUWZJ1n7uHbF4b9II" // Replace with your valid API key

    suspend fun askRecipeQuestion(recipe: Recipe, question: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (question.isEmpty()) {
                    return@withContext Result.failure(Exception("Câu hỏi không hợp lệ hoặc rỗng"))
                }

                // Construct prompt with recipe context
                val recipeContext = """
                    Công thức: ${recipe.title}
                    Nguyên liệu: ${recipe.extendedIngredients?.joinToString { it.original ?: it.name }}
                    Hướng dẫn: ${recipe.analyzedInstructions?.flatMap { it.steps ?: emptyList() }?.joinToString { it.step }}
                    Thời gian chuẩn bị: ${recipe.readyInMinutes} phút
                    Khẩu phần: ${recipe.servings}
                    
                    Câu hỏi: $question
                    Trả lời bằng tiếng Việt, ngắn gọn và chính xác.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", recipeContext)
                                })
                            })
                        })
                    })
                }

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    requestJson.toString()
                )

                val response = geminiApiService.generateContent(apiKey, requestBody)

                if (response.isSuccessful) {
                    response.body()?.let { geminiResponse ->
                        val result = parseGeminiResponse(geminiResponse)
                        if (result.isNotEmpty()) {
                            Result.success(result)
                        } else {
                            Result.failure(Exception("Không nhận được kết quả từ API"))
                        }
                    } ?: Result.failure(Exception("Không có phản hồi từ API"))
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Không có chi tiết lỗi"
                    Result.failure(Exception("Lỗi API: ${response.code()} - ${response.message()}, Chi tiết: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi: ${e.localizedMessage ?: e.toString()}"))
            }
        }
    }

    private fun parseGeminiResponse(response: GeminiResponse): String {
        return try {
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: response.error?.message
                ?: "Không nhận được kết quả từ API."
        } catch (e: Exception) {
            "Lỗi khi phân tích phản hồi: ${e.localizedMessage ?: e.toString()}"
        }
    }
}