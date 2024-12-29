package com.example.yeniproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentRecipeDetailBinding
import com.google.firebase.database.FirebaseDatabase
class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)

        val recipeId = arguments?.getString("id")
        val recipeName = arguments?.getString("name")
        val recipeMaterials = arguments?.getString("materials")
        val recipePreparation = arguments?.getString("preparation")
        val userId = arguments?.getString("userId")

        // Display recipe details
        binding.recipeNameText.text = recipeName
        binding.recipeMaterialsText.text = recipeMaterials
        binding.recipePreparationText.text = recipePreparation

        // Handle favorite icon click
        binding.favouriteIcon.setOnClickListener {
            if (userId != null && recipeId != null) {
                // User is logged in, save to favorites
                database.child("Users").child(userId).child("favorites").child(recipeId).setValue(true)
                    .addOnSuccessListener {
                        // Successfully added to favorites
                        binding.favouriteIcon.setImageResource(R.drawable.ic_favourite) // Filled heart icon
                        binding.favouriteIcon.setColorFilter(android.R.color.holo_red_dark)
                        Toast.makeText(context, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        // Error occurred while adding to favorites
                        Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // User is not logged in or recipeId is missing
                Toast.makeText(context, "User ID or Recipe ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
