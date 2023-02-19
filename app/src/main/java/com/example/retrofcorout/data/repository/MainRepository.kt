package com.example.retrofcorout.data.repository

import com.example.retrofcorout.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getUsers() = apiHelper.getUsers()
}