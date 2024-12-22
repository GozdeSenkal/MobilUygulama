package com.example.yeniproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.yeniproje.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    lateinit var userViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        binding.registerButton.setOnClickListener {
            val name = binding.fullNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(context, "Parolalar eşleşmiyor", Toast.LENGTH_SHORT).show()
            } else {
                // Add the new user to the database
                val newUser = User(name = name, email = email)
                CoroutineScope(Dispatchers.IO).launch {
                    (activity as? MainActivity)?.insertUser(newUser)

                    withContext(Dispatchers.Main) {
                        // Show success message and navigate to MyProfileFragment
                        Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, MyProfileFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }

        return binding.root
    }

}
