package com.example.yeniproje

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    //@Insert: Veritabanına veri eklemek için kullanılır.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    //@Update: Var olan veriyi güncellemek için kullanılır.
    @Update
    suspend fun updateUser(user: User)

    //@Delete: Veriyi silmek için kullanılır.
    @Delete
    suspend fun deleteUser(user: User)
 //
    //@Query: SQL sorgusu çalıştırmak için kullanılır.
    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()
}
