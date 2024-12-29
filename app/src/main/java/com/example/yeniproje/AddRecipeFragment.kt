package com.example.yeniproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentAddRecipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddRecipeFragment : Fragment() {

    private lateinit var binding: FragmentAddRecipeBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false)

        binding.addRecipeButton.setOnClickListener {
            val recipeName = binding.recipeNameEditText.text.toString().trim()
            val materials = binding.materialsEditText.text.toString().trim()
            val preparation = binding.preparationEditText.text.toString().trim()

            if (recipeName.isNotEmpty() && materials.isNotEmpty() && preparation.isNotEmpty()) {
                saveRecipeToFirebase(recipeName, materials, preparation)
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun saveRecipeToFirebase(recipeName: String, materials: String, preparation: String) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val userName = snapshot.child("name").getValue(String::class.java) ?: "Bilinmeyen Yazar"

            val recipeData = hashMapOf(
                "name" to recipeName,
                "materials" to materials,
                "preparation" to preparation,
                "userId" to userId,
                "author" to userName
            )

            val recipeRef = database.child("Recipes").push()
            recipeRef.setValue(recipeData)
                .addOnSuccessListener {
                    val recipeId = recipeRef.key
                    val userRecipesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("recipes")
                    userRecipesRef.push().setValue(recipeId)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Tarif başarıyla eklendi", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Tarif kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Tarif kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
