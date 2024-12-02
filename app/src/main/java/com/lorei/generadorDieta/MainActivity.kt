package com.lorei.generadorDieta

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lorei.generadorDieta.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
                /*R.id.settings -> {
                    loadFragment(SettingFragment())
                    true
                }*/
                else -> {true}
            }
        }
    }


}