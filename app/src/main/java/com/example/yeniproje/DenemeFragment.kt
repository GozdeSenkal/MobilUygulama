package com.example.yeniproje

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniproje.databinding.FragmentDenemeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DenemeFragment : Fragment() {

    private lateinit var binding: FragmentDenemeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private val favouriteRecipes = mutableListOf<Recipe>()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the current user ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDenemeBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView
        favouriteAdapter = FavouriteAdapter(favouriteRecipes)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = favouriteAdapter

        loadFavoriteRecipes()

        return binding.root
    }

    private fun loadFavoriteRecipes() {
        userId?.let { userId ->
            val favouritesRef = database.child("Users").child(userId).child("favorites")

            favouritesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favouriteRecipes.clear() // Clear the list before adding new data

                    for (dataSnapshot in snapshot.children) {
                        val recipeId = dataSnapshot.key // Use the key as the recipe ID
                        val isFavorite = dataSnapshot.getValue(Boolean::class.java) ?: false

                        if (isFavorite && recipeId != null) {
                            loadRecipeDetails(recipeId)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DenemeFragment", "Error fetching favorites: ${error.message}")
                }
            })
        }
    }

    private fun loadRecipeDetails(recipeId: String) {
        val recipeRef = database.child("Recipes").child(recipeId)

        recipeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipe = snapshot.getValue(Recipe::class.java)
                recipe?.let {
                    favouriteRecipes.add(it) // Add recipe to the list
                    favouriteAdapter.notifyDataSetChanged() // Update the RecyclerView
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DenemeFragment", "Error fetching recipe details: ${error.message}")
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DenemeFragment().apply {
                arguments = Bundle().apply {
                    // putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}
