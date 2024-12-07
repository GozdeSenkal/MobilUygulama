package com.example.yeniproje

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Giriş Yap butonunun tıklanma olayını dinleme
        binding.loginButton.setOnClickListener {
            // Giriş işlemi (örnek olarak kullanıcıyı giriş yaptı olarak kabul ediyoruz)
            val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

            // Giriş yapıldığında MyProfileFragment'e geçiş
            val fragment = MyProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

            // Toolbar başlığını güncelle
            (activity as MainActivity).supportActionBar?.title = "My Profile"
        }

        // Kayıt Ol butonuna tıklanması durumunda RegistrationFragment'e geçiş
        binding.registerButton.setOnClickListener {
            val fragment = RegistrationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar başlığını "Profile" olarak ayarla
        (activity as MainActivity).updateToolbarTitle("Profile")
    }


}

