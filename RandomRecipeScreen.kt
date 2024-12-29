package com.example.yeniproje

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun RandomRecipeScreenWithShake(context: Context, lifecycleOwner: LifecycleOwner) {
    var recipeName by remember { mutableStateOf("Tarif İsmi") }
    var materials by remember { mutableStateOf("Malzemeler burada gösterilecek.") }
    var preparation by remember { mutableStateOf("Hazırlık aşamaları burada gösterilecek.") }
    var isLoading by remember { mutableStateOf(false) }

    val database = FirebaseDatabase.getInstance().reference.child("Recipes")

    // Tarifi yükleme
    LaunchedEffect(Unit) {
        loadRandomRecipe(database, { recipeName = it }, { materials = it }, { preparation = it }, { isLoading = it })
    }

    // Sallama algılayıcıyı başlat
    ShakeDetector(context, lifecycleOwner) {
        loadRandomRecipe(database, { recipeName = it }, { materials = it }, { preparation = it }, { isLoading = it })
    }

    // UI
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = recipeName,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Malzemeler: $materials",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Hazırlık: $preparation",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {

                loadRandomRecipe(database, { recipeName = it }, { materials = it }, { preparation = it }, { isLoading = it })
            }) {
                Text("Günün Tarifini Görmek İçin Telefonu Salla")
            }
        }
    }
}

// Tarif verisini Firebase'den al
fun loadRandomRecipe(
    database: DatabaseReference,
    updateRecipeName: (String) -> Unit,
    updateMaterials: (String) -> Unit,
    updatePreparation: (String) -> Unit,
    updateLoading: (Boolean) -> Unit
) {
    updateLoading(true)

    // Veritabanından veri alırken eklenen log
    database.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val snapshot = task.result
            if (snapshot.exists()) {
                val recipes = snapshot.children.toList()
                val randomIndex = Random.nextInt(recipes.size)
                val randomRecipe = recipes[randomIndex]

                // Tarifi logla
                Log.d("RecipeFetch", "Random Recipe: ${randomRecipe.child("name").value.toString()}")

                updateRecipeName(randomRecipe.child("name").value.toString())
                updateMaterials(randomRecipe.child("materials").value.toString())
                updatePreparation(randomRecipe.child("preparation").value.toString())
            } else {
                Log.e("RecipeFetch", "No recipes found.")
                updateRecipeName("Tarif Bulunamadı")
                updateMaterials("")
                updatePreparation("")
            }
        } else {
            Log.e("RecipeFetch", "Error: ${task.exception?.message}")
            updateRecipeName("Hata Oluştu")
            updateMaterials(task.exception?.message ?: "Bilinmeyen hata")
            updatePreparation("")
        }
        updateLoading(false)
    }
}

// ShakeDetector: Telefonu sallama algılayıcı sınıfı
class ShakeDetector(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime = 0L

    init {
        // Sensor kaydını onResume içinde yapmak daha sağlıklı olabilir
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    // Senkronizasyonu sağlayacak şekilde Sensor kaydını sürekli tutalım
    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
            val acceleration = sqrt(x * x + y * y + z * z)

            val now = System.currentTimeMillis()
            if (acceleration > 15 && now - lastShakeTime > 1000) { // Sallama hassasiyeti ve süre
                lastShakeTime = now
                Log.d("ShakeDetector", "Shake detected!")
                onShake() // Sallama algılandığında tarifi yükle
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Lifecycle metotlarında sensör dinleyicisini başlatma ve durdurma
    fun onResume() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }
}
