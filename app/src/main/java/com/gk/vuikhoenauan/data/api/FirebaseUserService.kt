package com.gk.vuikhoenauan.data.repository

import android.util.Log
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseUserService {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val usersRef: DatabaseReference = db.child("users")
    private val TAG = "FirebaseUserService"

    suspend fun addUser(uid: String, user: User) {
        try {
            usersRef.child(uid).setValue(user).await()
            Log.d(TAG, "addUser: Successfully saved user $uid to Realtime Database")
        } catch (e: Exception) {
            Log.e(TAG, "addUser: Failed to save user $uid: ${e.message}", e)
            throw e
        }
    }

    suspend fun getUser(uid: String): User? {
        try {
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            Log.d(TAG, "getUser: Retrieved user $uid: ${user?.email}")
            return user
        } catch (e: Exception) {
            Log.e(TAG, "getUser: Failed to retrieve user $uid: ${e.message}", e)
            return null
        }
    }

    suspend fun getAllUsers(): List<User> {
        try {
            val snapshot = usersRef.get().await()
            val userList = mutableListOf<User>()
            snapshot.children.forEach {
                it.getValue(User::class.java)?.let { user ->
                    userList.add(user)
                }
            }
            Log.d(TAG, "getAllUsers: Retrieved ${userList.size} users")
            return userList
        } catch (e: Exception) {
            Log.e(TAG, "getAllUsers: Failed to retrieve users: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun updateUser(uid: String, username: String?, email: String?, avatar: String?, password: String?) {
        try {
            val updates = mutableMapOf<String, Any>()
            username?.let { updates["username"] = it }
            email?.let { updates["email"] = it }
            avatar?.let { updates["avatar"] = it }
            password?.let { updates["password"] = it }
            if (updates.isNotEmpty()) {
                usersRef.child(uid).updateChildren(updates).await()
                Log.d(TAG, "updateUser: Successfully updated user $uid")
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateUser: Failed to update user $uid: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteUser(uid: String) {
        try {
            usersRef.child(uid).removeValue().await()
            Log.d(TAG, "deleteUser: Successfully deleted user $uid")
        } catch (e: Exception) {
            Log.e(TAG, "deleteUser: Failed to delete user $uid: ${e.message}", e)
            throw e
        }
    }

    suspend fun addFavoriteRecipe(uid: String, recipe: Recipe) {
        try {
            usersRef.child(uid)
                .child("favoriteRecipes")
                .child(recipe.id.toString())
                .setValue(recipe)
                .await()
            Log.d(TAG, "addFavoriteRecipe: Successfully added recipe ${recipe.id} for user $uid")
        } catch (e: Exception) {
            Log.e(TAG, "addFavoriteRecipe: Failed to add recipe for user $uid: ${e.message}", e)
            throw e
        }
    }

    suspend fun getFavoriteRecipes(uid: String): List<Recipe> {
        try {
            val snapshot = usersRef.child(uid).child("favoriteRecipes").get().await()
            val recipeList = mutableListOf<Recipe>()
            snapshot.children.forEach { child ->
                try {
                    val recipe = child.getValue(Recipe::class.java)
                    if (recipe != null) {
                        recipeList.add(recipe)
                    } else {
                        Log.w(TAG, "getFavoriteRecipes: Failed to parse recipe for key ${child.key}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "getFavoriteRecipes: Error parsing recipe for key ${child.key}: ${e.message}", e)
                }
            }
            Log.d(TAG, "getFavoriteRecipes: Retrieved ${recipeList.size} favorite recipes for user $uid")
            return recipeList
        } catch (e: Exception) {
            Log.e(TAG, "getFavoriteRecipes: Failed to retrieve favorite recipes for user $uid: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun removeFavoriteRecipe(uid: String, recipeId: String) {
        try {
            usersRef.child(uid)
                .child("favoriteRecipes")
                .child(recipeId)
                .removeValue()
                .await()
            Log.d(TAG, "removeFavoriteRecipe: Successfully removed recipe $recipeId for user $uid")
        } catch (e: Exception) {
            Log.e(TAG, "removeFavoriteRecipe: Failed to remove recipe $recipeId for user $uid: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateFavoriteTopics(uid: String, topics: Map<String, Int>) {
        try {
            usersRef.child(uid).child("favoriteTopics").setValue(topics).await()
            Log.d(TAG, "updateFavoriteTopics: Successfully updated topics for user $uid")
        } catch (e: Exception) {
            Log.e(TAG, "updateFavoriteTopics: Failed to update topics for user $uid: ${e.message}", e)
            throw e
        }
    }
}