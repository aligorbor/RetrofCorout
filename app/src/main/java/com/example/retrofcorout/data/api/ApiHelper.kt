package com.example.retrofcorout.data.api

import com.example.retrofcorout.data.model.User

class ApiHelper (private val apiService: ApiService) {

    suspend fun getUsers() = apiService.getUsers()
    suspend fun getUser(userId: String) = apiService.getUser(userId)
    suspend fun postUser(user: User) = apiService.postUser(user)
    suspend fun putUser(user: User) = apiService.putUser(user.id, user)
    suspend fun delUser(userId: String) = apiService.delUser(userId)
}