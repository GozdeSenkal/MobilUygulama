package com.example.yeniproje

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("myRecipes_table")
data class MyRecipes(

   @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String,
    val materials: List<String>,
    val preparation: List<String>
)
