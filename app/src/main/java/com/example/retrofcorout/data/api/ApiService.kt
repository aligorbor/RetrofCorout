package com.example.retrofcorout.data.api

import com.example.retrofcorout.data.model.User
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}