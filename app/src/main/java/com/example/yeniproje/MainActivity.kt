package com.example.yeniproje

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.yeniproje.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // Toolbar'ı ayarla

        // Başlığı sola dayalı olarak ayarla
        supportActionBar?.apply {
            title = "Home" // Başlık, her fragment için farklı olacak şekilde güncellenebilir
            setDisplayHomeAsUpEnabled(true) // Geribildirim için "geri" ikonunu göster
        }

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        if (savedInstanceState == null) {
            loadHomeFragment()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadHomeFragment()
                R.id.search -> loadSearchFragment()
                R.id.profile -> loadProfileFragment()
                else -> false
            }
        }

        


    }



    private fun loadHomeFragment(): Boolean {
        val fragment = HomeFragment()
        updateToolbarTitle("Home")
        return loadFragment(fragment)
    }

    private fun loadSearchFragment(): Boolean {
        val fragment = SearchFragment()
        updateToolbarTitle("Search")
        return loadFragment(fragment)
    }

    private fun loadProfileFragment(): Boolean {
        val fragment = if (isUserLoggedIn()) {
            MyProfileFragment()  // Giriş yapmışsa MyProfileFragment'i yükle
        } else {
            ProfileFragment()  // Giriş yapmamışsa ProfileFragment'i yükle
        }
        updateToolbarTitle("Profile")
        return loadFragment(fragment)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showProfileOptionsDialog() {
        val options = arrayOf("Çıkış Yap", "Profil Bilgilerimi Güncelle")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> logOut() // Çıkış işlemi
                1 -> updateProfileInfo() // Profil güncelleme işlemi
            }
        }
        builder.show() // Diyaloğu göster
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
        loadProfileFragment()  // Giriş yapılmadığında ProfileFragment'ı yükle
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }


}
