package com.example.retrofcorout.data.api

import com.example.retrofcorout.data.model.User
import retrofit2.http.*

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): User

    @Headers("content-type: application/json")
    @POST("users")
    suspend fun postUser(@Body user: User): User

    @Headers("content-type: application/json")
    @PUT("users/{id}")
    suspend fun putUser(@Path("id") userId: String, @Body user: User): User

    @DELETE("users/{id}")
    suspend fun delUser(@Path("id") userId: String): User
}