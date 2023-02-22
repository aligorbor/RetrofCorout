package com.example.retrofcorout.data.repository

import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.model.User

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getUsers() = apiHelper.getUsers()
    suspend fun getUser(userId: String) = apiHelper.getUser(userId)
    suspend fun postUser(user: User) = apiHelper.postUser(user)
    suspend fun putUser(user: User) = apiHelper.putUser(user)
    suspend fun delUser(userId: String) = apiHelper.delUser(userId)
}