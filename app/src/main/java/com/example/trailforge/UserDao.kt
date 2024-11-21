package com.example.trailforge

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}