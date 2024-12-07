package com.example.yeniproje

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.yeniproje.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        // Kayıt Ol butonunun tıklanma olayını dinleme
        binding.registerButton.setOnClickListener {
            // Kayıt işlemi yapıldı (örneğin, giriş yapmış gibi kabul ediyoruz)
            val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

            // Kayıt olduktan sonra MyProfileFragment'e geçiş
            val fragment = MyProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

            // Toolbar başlığını güncelle
            (activity as MainActivity).updateToolbarTitle("My Profile")

            // BottomNavigationView'de "Profile" butonunu "My Profile" olarak güncelle
            //(activity as MainActivity).updateBottomNavToMyProfile()
        }

        return binding.root
    }
}
