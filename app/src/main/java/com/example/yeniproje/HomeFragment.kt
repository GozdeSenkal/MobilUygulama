package com.example.yeniproje

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniproje.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipeList = mutableListOf<Recipe>()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        recipeRecyclerView = binding.recipeRecyclerView
        recipeAdapter = RecipeAdapter(recipeList) { recipe -> onRecipeClick(recipe) }
        recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        recipeRecyclerView.adapter = recipeAdapter

        loadRecipes()

        return binding.root
    }

    private fun loadRecipes() {
        val recipesRef = database.child("Recipes")

        recipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear() // Listeyi temizle
                for (dataSnapshot in snapshot.children) {
                    val recipe = dataSnapshot.getValue(Recipe::class.java)
                    recipe?.let { recipeList.add(it) }
                }
                recipeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Tarifler yüklenirken hata oluştu: ${error.message}")
            }
        })
    }

    private fun onRecipeClick(recipe: Recipe) {
        // Passing the recipe ID along with other details
        val bundle = Bundle().apply {
            putString("id", recipe.id)  // Pass the recipe ID
            putString("name", recipe.name)
            putString("materials", recipe.materials)
            putString("preparation", recipe.preparation)
            putString("userId", recipe.author)  // Pass the user's ID who created the recipe
        }

        val recipeDetailFragment = RecipeDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, recipeDetailFragment)
            .addToBackStack(null)
            .commit()
    }


}