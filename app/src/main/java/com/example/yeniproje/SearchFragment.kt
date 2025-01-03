package com.example.yeniproje

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yeniproje.databinding.FragmentSearchBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var tts: TextToSpeech

    // Kullanıcının fotoğraf çekebilmesi için izin kontrolü
    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Log.e("Permission", "Camera permission denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Text-to-Speech başlatma
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
            }
        }

        // Fotoğraf çekme butonuna tıklama işlemi
        binding.btnCapture.setOnClickListener {
            checkCameraPermission()
        }

        return binding.root
    }

    // Kamera izni kontrolü
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    // Kamera açma
    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 100)
    }

    // Fotoğrafın çekilmesinden sonra veriyi işle
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            labelImage(imageBitmap)
        }
    }

    // Fotoğrafı etiketleme ve sesli yanıt verme
    private fun labelImage(imageBitmap: Bitmap) {
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap)
        val labeler: FirebaseVisionImageLabeler = FirebaseVision.getInstance().onDeviceImageLabeler

        labeler.processImage(firebaseVisionImage)
            .addOnSuccessListener { labels ->
                if (labels.isNotEmpty()) {
                    val label = labels[0]
                    val foodName = label.text
                    val confidence = label.confidence

                    // Güven değeri yeterliyse yemeği sesli yanıtla ve ekranda göster
                    if (confidence > 0.8) {
                        speakFoodName(foodName)
                        displayFoodName(foodName)
                    } else {
                        speakFoodName("Sorry, I couldn't identify the food")
                        displayFoodName("Sorry, I couldn't identify the food")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseMLKit", "Error labeling image", e)
            }
    }

    // Yemeğin adını sesli yanıt verme
    private fun speakFoodName(foodName: String) {
        tts.speak("This looks like $foodName", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Yemeğin adını ekranda gösterme
    private fun displayFoodName(foodName: String) {
        binding.textView3.text = foodName
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
