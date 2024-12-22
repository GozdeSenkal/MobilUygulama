package com.example.yeniproje

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
@Database(
    entities = [UserDatabase::class],
    version=1
)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao() : UserDao

    companion object{
        private var INSTANCE : UserDatabase? = null

        fun getUserDatabase(context: Context): UserDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE= instance
                instance
            }
        }
    }
}