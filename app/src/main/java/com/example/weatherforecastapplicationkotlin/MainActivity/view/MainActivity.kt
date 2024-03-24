package com.example.weatherforecastapplicationkotlin.MainActivity.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecastapplicationkotlin.R
import com.google.android.material.navigation.NavigationView

 class MainActivity : AppCompatActivity()  {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menu: ImageView
    private final var TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu = findViewById(R.id.fab_menu)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)
        navigationView = findViewById(R.id.navigation_view)
        menu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navigationView, navController)

    }
}