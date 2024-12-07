package com.example.yeniproje

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentSettingsBinding
import androidx.activity.result.contract.ActivityResultContracts

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    // Resim seçme işlemi için launcher
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileImageView.setImageURI(it) // Seçilen resmi profileImageView'de göster
                // Burada seçilen resmi kaydetme işlemi yapılabilir
            } ?: Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }

    // Fragment'in görünümü oluşturulurken
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FragmentSettingsBinding ile bağlama
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Resim seçme butonuna tıklanma işlemi
        binding.selectImageButton.setOnClickListener {
            // Galeriye gitmek için image/* tipiyle açma
            pickImageLauncher.launch("image/*")
        }

        return binding.root
    }
}
