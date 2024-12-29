package com.example.yeniproje

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentMyProfileBinding
import androidx.activity.compose.setContent

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        // Toolbar başlığını ayarlama
        (activity as MainActivity).supportActionBar?.title = "My Profile"

        // Butonlara tıklama olaylarını bağlama
        binding.viewRecipesButton.setOnClickListener {
            // Tarifleri Gör butonuna tıklanınca yapılacak işlemler
            // Örneğin, başka bir fragmenta geçiş yapmak
            //findNavController().navigate(R.id.action_myProfileFragment_to_viewRecipesFragment)
        }

        binding.addRecipeButton.setOnClickListener {
            // Tarif Ekle butonuna tıklanınca yapılacak işlemler
            val addRecipeFragment = AddRecipeFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, addRecipeFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.randomRecipe.setOnClickListener {
            // Activity'de doğrudan Compose içeriğini setContent ile değiştiriyoruz
            requireActivity().setContent {
                RandomRecipeScreenWithShake(
                    context = requireContext(),
                    lifecycleOwner = viewLifecycleOwner
                ) // RandomRecipeScreenWithShake Compose fonksiyonu çağrılıyor
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar başlığını "My Profile" olarak ayarla
        (activity as MainActivity).updateToolbarTitle("My Profile")
    }
}
