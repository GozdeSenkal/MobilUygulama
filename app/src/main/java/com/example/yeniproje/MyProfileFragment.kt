package com.example.yeniproje

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        // Toolbar başlığını ayarlama
        (activity as MainActivity).supportActionBar?.title = "My Profile"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar başlığını "My Profile" olarak ayarla
        (activity as MainActivity).updateToolbarTitle("My Profile")
    }
}
