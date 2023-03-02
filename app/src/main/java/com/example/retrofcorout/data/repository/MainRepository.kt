package com.example.retrofcorout.data.repository

import androidx.annotation.WorkerThread
import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.database.UserDao
import com.example.retrofcorout.data.model.User
import kotlinx.coroutines.flow.Flow

class MainRepository(private val apiHelper: ApiHelper, private val userDao: UserDao) {
    suspend fun getUsers() = apiHelper.getUsers()
    suspend fun getUser(userId: String) = apiHelper.getUser(userId)
    suspend fun postUser(user: User) = apiHelper.postUser(user)
    suspend fun putUser(user: User) = apiHelper.putUser(user)
    suspend fun delUser(userId: String) = apiHelper.delUser(userId)

    val getUsersDao: Flow<List<User>> = userDao.getAllUsers()
    fun getUserDao(userId: String): Flow<User> = userDao.getUser(userId)

    @WorkerThread
    suspend fun insertUserDao(user: User) {
        userDao.insertUser(user)
    }

    @WorkerThread
    suspend fun updateUserDao(user: User) {
        userDao.updateUser(user)
    }

    @WorkerThread
    suspend fun deleteUserDao(user: User) {
        userDao.deleteUser(user)
    }

    @WorkerThread
    suspend fun deleteAllUsersDao() {
        userDao.deleteAllUsers()
    }

}