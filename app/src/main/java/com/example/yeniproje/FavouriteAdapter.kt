package com.example.yeniproje

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yeniproje.databinding.ItemRecipeBinding

class FavouriteAdapter(
    private var favouriteRecipes: List<Recipe> // Changed to List for immutability
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    inner class FavouriteViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.name
            binding.recipeAuthor.text = recipe.author

            // Load the image using Glide
            Glide.with(binding.recipeImage.context)
                .load(recipe.imageUrl)
                .into(binding.recipeImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val recipe = favouriteRecipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = favouriteRecipes.size

    // Method to update the list
    fun updateData(newRecipes: List<Recipe>) {
        favouriteRecipes = newRecipes // Replace the old list with the new one
        notifyDataSetChanged()
    }
}
