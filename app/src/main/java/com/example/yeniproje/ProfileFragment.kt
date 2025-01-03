package com.example.yeniproje

import RegistrationFragment
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.SharedPreferences

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // FirebaseAuth başlat
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)

        // Giriş Yap butonunun tıklanma olayını dinleme
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserCredentials(email, password)
                            val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                            Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                            // MyProfileFragment'e yönlendir
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, MyProfileFragment())
                                .commit()
                        } else {
                            Toast.makeText(context, "Hata: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }

        // Kayıt Ol butonuna tıklanması durumunda RegistrationFragment'e geçiş
        binding.registerButton.setOnClickListener {
            val fragment = RegistrationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

        }

        // Google Sign-In yapılandırması
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Google giriş butonuna tıklama işlemi
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar başlığını "Profile" olarak ayarla
        (activity as MainActivity).updateToolbarTitle("Profile")
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Firebase ile giriş
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        // Google ile başarılı giriş yapılınca yapılacak işlemler
                        val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        Toast.makeText(context, "Google ile giriş başarılı!", Toast.LENGTH_SHORT).show()
                        // MyProfileFragment'e yönlendir
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, MyProfileFragment())
                            .commit()
                    } else {
                        Toast.makeText(context, "Google ile giriş başarısız: ${authResult.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun saveUserCredentials(email: String, password: String) {
        // Kullanıcı bilgilerini SharedPreferences'e kaydediyoruz
        sharedPreferences.edit().apply {
            putString("userEmail", email)
            putString("userPassword", password)  // Düz metin olarak kaydediliyor
            apply()
        }
    }
}
