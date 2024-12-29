import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.yeniproje.MyProfileFragment
import com.example.yeniproje.R
import com.example.yeniproje.User
import com.example.yeniproje.UserViewModel
import com.example.yeniproje.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        binding.registerButton.setOnClickListener {
            val name = binding.fullNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(context, "Parolalar eşleşmiyor", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, email, password)
            }
        }

        return binding.root
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = mapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password, // Daha güvenli bir şifreleme yöntemi kullanabilirsiniz.
                        "favorite" to emptyList<String>(),
                        "recipe" to emptyList<String>()
                    )

                    userId?.let {
                        database.child("Users").child(it).setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Room Database'e kullanıcıyı ekleme
                                    val newUser = User(name = name, email = email)
                                    userViewModel.insertUser(newUser)

                                    val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainerView, MyProfileFragment())
                                        .addToBackStack(null)
                                        .commit()
                                } else {
                                    Toast.makeText(context, "Veritabanına kaydedilemedi!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Kayıt başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
