package com.example.retrofcorout.data.api

class ApiHelper (private val apiService: ApiService) {

    suspend fun getUsers() = apiService.getUsers()

}