package com.example.yeniproje

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
    val name: String,
    val email: String
)
