package com.example.retrofcorout.data.database

import androidx.room.*
import com.example.retrofcorout.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUser(userId: String): Flow<User>

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

}