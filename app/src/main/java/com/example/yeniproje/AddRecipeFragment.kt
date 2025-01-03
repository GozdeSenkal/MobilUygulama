package com.example.yeniproje

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.FragmentAddRecipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddRecipeFragment : Fragment() {

    private lateinit var binding: FragmentAddRecipeBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false)

        binding.addRecipeButton.setOnClickListener {
            val recipeName = binding.recipeNameEditText.text.toString().trim()
            val materials = binding.materialsEditText.text.toString().trim()
            val preparation = binding.preparationEditText.text.toString().trim()

            if (recipeName.isNotEmpty() && materials.isNotEmpty() && preparation.isNotEmpty()) {
                if (selectedImageUri != null) {
                    saveRecipeToFirebase(recipeName, materials, preparation)
                } else {
                    Toast.makeText(requireContext(), "Lütfen bir resim seçin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImageButton.setOnClickListener {
            // Resim seçmek için galeriye gitme
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, IMAGE_PICK_CODE)
        }

        return binding.root
    }

    // Resim seçildiğinde bu fonksiyon çağrılır
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            binding.selectedImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveRecipeToFirebase(recipeName: String, materials: String, preparation: String) {
        val userId = auth.currentUser?.uid ?: return

        // Firebase Database'e tarif verisini kaydetme
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val userName = snapshot.child("name").getValue(String::class.java) ?: "Bilinmeyen Yazar"

            val recipeData = hashMapOf(
                "name" to recipeName,
                "materials" to materials,
                "preparation" to preparation,
                "userId" to userId,
                "author" to userName
            )

            val recipeRef = database.child("Recipes").push()
            recipeRef.setValue(recipeData)
                .addOnSuccessListener {
                    // Firebase Storage'e resim yükleme
                    selectedImageUri?.let { uri ->
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        val imageName = "recipe_image_${System.currentTimeMillis()}"
                        saveImageToStorage(bitmap, imageName)
                    }

                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Tarif kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, recipeId: String) {
        val storageReference = storage.reference
        val imageRef = storageReference.child("recipes_images/$recipeId.jpg")

        // Resmi Firebase Storage'a yükleme
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                // Resim URL'sini Firebase Realtime Database'e kaydetme
                val recipeRef = database.child("Recipes").child(recipeId)
                recipeRef.child("imageUrl").setValue(imageUrl)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Tarif başarıyla eklendi", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Resim kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Resim yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap, imageName: String) {
        try {
            val contentResolver = requireContext().contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourAppName") // Kaydedilecek klasör
            }

            // Resmi kaydedeceğimiz URI'yi oluşturuyoruz
            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            // Eğer URI başarıyla oluşturulursa, OutputStream açıyoruz
            if (imageUri != null) {
                val outputStream = contentResolver.openOutputStream(imageUri)

                // Resmi OutputStream'a yazıyoruz
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()

                    // Resim başarıyla kaydedildiğinde URI'yi alıyoruz ve ImageView'da gösteriyoruz
                    binding.selected2ImageView.setImageURI(imageUri)

                    Toast.makeText(requireContext(), "Resim başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "OutputStream oluşturulamadı", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Resim URI'si oluşturulamadı", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Resim kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
