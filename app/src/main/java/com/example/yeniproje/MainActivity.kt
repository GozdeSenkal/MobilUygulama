package com.example.yeniproje

import HomeScreen
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    // Favourite için liste ve fragment
    private val favouriteItems = mutableListOf<String>() // Örnek olarak String kullanıyoruz
    private val favouriteFragment = FavouriteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }


        setSupportActionBar(binding.toolbar1) // Toolbar'ı ayarla

        // Toolbar başlığını sola dayalı olarak ayarla
        supportActionBar?.apply {
            title = "Home"
            setDisplayHomeAsUpEnabled(true) // Geri ikonu gösterme
        }

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        if (savedInstanceState == null) {
            loadHomeFragment()
        }




        // BottomNavigationView tıklamalarını dinleme
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadHomeFragment()
                R.id.search -> loadSearchFragment()
                R.id.profile -> loadProfileFragment()
                R.id.favourite -> loadFavouriteFragment()
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            sendNotificationBroadcast()
        }

    }

    // Broadcast gönderen metot
    private fun sendNotificationBroadcast() {
        val intent = Intent(this, NotificationReceiver::class.java)
        sendBroadcast(intent)
    }

    // AlarmManager ile belirli bir süre sonra bildirim gönderme
    private fun scheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val triggerTime = System.currentTimeMillis() + 60000 // 60 saniye sonra
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun loadHomeFragment():Boolean{
        val fragment = HomeFragment()
        updateToolbarTitle("Home")
        return loadFragment(fragment)
//         Compose ekranını başlatmak için
//        setContent {
//            HomeScreen()
//        }
//        updateToolbarTitle("Home")
//        return true
    }

    private fun loadSearchFragment(): Boolean {
        val fragment = SearchFragment()
        updateToolbarTitle("Search")
        return loadFragment(fragment)
    }

    private fun loadProfileFragment(): Boolean {
        val fragment = if (isUserLoggedIn()) {
            MyProfileFragment()  // Giriş yapmışsa MyProfileFragment
        } else {
            ProfileFragment()  // Giriş yapmamışsa ProfileFragment
        }
        updateToolbarTitle("Profile")
        return loadFragment(fragment)
    }

    private fun loadFavouriteFragment(): Boolean {
        updateToolbarTitle("Favourite")
        return loadFragment(favouriteFragment)
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                // Seçenekleri göstermek için bir metod çağıracağız
                showProfileOptionsDialog()
                true
            }
            R.id.sendNotification -> {
                // Bildirim göndermek için menü seçeneği
                sendNotificationBroadcast()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showProfileOptionsDialog() {
        val options = arrayOf("Çıkış Yap", "Profil Bilgilerimi Güncelle")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> logOut()
                1 -> updateProfileInfo()
            }
        }
        builder.show()
    }

    private fun updateProfileInfo() {
        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun logOut() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        loadProfileFragment()  // Giriş yapılmadığında ProfileFragment yükle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    fun insertUser(newUser: User) {
        userViewModel.insertUser(newUser)
        return
    }


}
