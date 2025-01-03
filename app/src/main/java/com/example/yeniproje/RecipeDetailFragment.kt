package com.example.yeniproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentRecipeDetailBinding
import com.google.firebase.auth.FirebaseAuth
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
            val recipeId = arguments?.getString("id") // Tarife ait ID'yi doğru şekilde aldığınızdan emin olun
            val userId = FirebaseAuth.getInstance().currentUser?.uid // Oturum açmış kullanıcının ID'si

            if (userId != null && recipeId != null) {
                // Users tablosundan kullanıcı referansını al
                val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                // Favorilere ekleme işlemi
                val favoriteRef = userRef.child("favorites").child(recipeId)
                favoriteRef.setValue(true)
                    .addOnSuccessListener {
                        binding.favouriteIcon.setImageResource(R.drawable.ic_favourite)
                        Toast.makeText(requireContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Favorilere ekleme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Kullanıcı veya tarif bilgisi eksik!", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }
}