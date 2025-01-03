package com.example.yeniproje

data class Recipe(
    val id: String = "",  // Recipe ID
    val name: String = "",
    val materials: String = "",
    val preparation: String = "",
    val author: String = "",
    val imageUrl: String = "" // New field for image URL
)
