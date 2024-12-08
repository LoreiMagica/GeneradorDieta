package com.lorei.generadorDieta

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lorei.generadorDieta.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref =  getSharedPreferences("Settings", Context.MODE_PRIVATE)
        applySavedLocale()  // Aplicar el idioma guardado antes de llamar a super.onCreate()


        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Ocultar ActionBar

        binding = ActivityMainBinding.inflate(layoutInflater)
        // Aplicar el tema guardado antes de setContentView
        if (sharedPref!!.getBoolean("isDarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(binding.root)



        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dieta, R.id.navigation_receta, R.id.navigation_ajustes
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_dieta -> {
                    this.findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.nav_dieta)
                    true
                }
                R.id.navigation_receta -> {
                    this.findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.nav_receta)
                    true
                }
                R.id.navigation_ajustes -> {
                    this.findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.nav_menu_opciones)
                    true
                }
                else -> {true}
            }
        }
    }
    private fun applySavedLocale() {
        val prefs: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("Language", "en") ?: "en"
        setLocale(languageCode)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}